package com.wxy.api.gateway.filter;

import com.wxy.api.common.model.entity.InterfaceInfo;
import com.wxy.api.common.model.entity.User;
import com.wxy.api.common.model.enums.HeaderNameType;
import com.wxy.api.common.service.InnerInterfaceInfoService;
import com.wxy.api.common.service.InnerRedisService;
import com.wxy.api.common.service.InnerUserInterfaceInfoService;
import com.wxy.api.common.service.InnerUserService;
import com.wxy.api.sdk.utils.CusAccessObjectUtil;
import com.wxy.api.sdk.utils.SignUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import static com.wxy.api.common.constant.TraceCheckConstant.*;

/**
 * 全局过滤
 * 全局请求拦截处理
 */
@Slf4j
@Component
public class CustomGlobalFilter implements GlobalFilter, Ordered
{

    @DubboReference(lazy = true)
    private InnerUserService innerUserService;

    @DubboReference(lazy = true)
    private InnerInterfaceInfoService innerInterfaceInfoService;

    @DubboReference(lazy = true)
    private InnerUserInterfaceInfoService innerUserInterfaceInfoService;

    @DubboReference(lazy = true)
    private InnerRedisService innerRedisService;

    private static final long FIVE_MINUTES = 60 * 5L;

    /**
     * 自定义过滤器(用户发送请求到API网关)
     *
     * @param exchange 路由交换机
     * @param chain 责任链（多个过滤器按照顺序串成链条）
     * @return Mono 异步操作
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain)
    {
        // 请求日志
        ServerHttpRequest request = exchange.getRequest();
        HttpHeaders headers = request.getHeaders();

        // 从headers拿请求的host
        String host = headers.getFirst(HeaderNameType.Interface_Host.getHeaderName());
        String path = host + request.getPath().value();
        String method = Objects.requireNonNull(request.getMethod()).toString();
        String sourceAddress = Objects.requireNonNull(request.getLocalAddress()).getHostString();
        log.info("请求唯一标识: {}", request.getId());
        log.info("请求路径: {}", path);
        log.info("请求方法: {}", method);
        log.info("请求参数: {}", request.getQueryParams());
        log.info("来源IP地址: {}", sourceAddress);

        // 访问控制 - 黑白名单 (请求太频繁了拒绝掉 -- 10s调用接口20次)
        ServerHttpResponse response = exchange.getResponse();
        if (innerRedisService.checkIPBlocked(sourceAddress)) {
            return handleNoAuth(response);
        }

        // 用户鉴权 (从请求头中获取的值)
        String accessKey = headers.getFirst(HeaderNameType.Access_Key.getHeaderName());
        String nonce = headers.getFirst(HeaderNameType.Nonce.getHeaderName());
        String timestamp = headers.getFirst(HeaderNameType.Timestamp.getHeaderName());
        String sign = headers.getFirst(HeaderNameType.Sign.getHeaderName());
        String body = headers.getFirst(HeaderNameType.Body.getHeaderName());
        String tempBody = headers.getFirst(HeaderNameType.Temp_Body.getHeaderName());
        String decodedString = "{}";
        if (!"{}".equals(tempBody) || body != null) {
            decodedString = URLDecoder.decode(body, StandardCharsets.UTF_8);
        }

        // 数据库中查是否权限已经分配给用户,用accessKey查
        User invokeUser = null;
        try {
            invokeUser = innerUserService.getInvokeUser(accessKey);
        }
        catch (Exception e) {
            log.info("网关获取用户信息失败，getInvokeUser error", e);
        }
        if (invokeUser == null) {
            return handleNoAuth(response);
        }

        // 校验随机数
        String ip = CusAccessObjectUtil.getClientIpFromServerWebExchange(exchange);
        String nonceKey = ip+"-"+path+"-"+method+"-"+HeaderNameType.Nonce.getHeaderName()+"-from-"+accessKey;
        log.info("nonce: {}", nonce);
        if (!innerRedisService.findParams(nonceKey, nonce)) {
            return handleNoAuth(response);
        }

        // 时间戳校验，时间和当前时间不能超过5分钟
        long currentTime = System.currentTimeMillis() / 1000;
        if ( timestamp == null || (currentTime - Long.parseLong(timestamp)) >= FIVE_MINUTES ) {
            return handleNoAuth(response);
        }

        // ak、sk校验
        String secretKey = invokeUser.getSecretKey();
        String serverSign = SignUtils.getSign(decodedString, secretKey);
        if (sign == null || !sign.equals(serverSign)) {
            return handleNoAuth(response);
        }

        // 请求的模拟接口是否存在,从数据库中查询(远程调用backend项目的inner接口)
        InterfaceInfo interfaceInfo = null;
        try {
            interfaceInfo = innerInterfaceInfoService.getInterfaceInfo(path, method);
        }
        catch (Exception e) {
            log.info("getInterfaceInfo error", e);
        }
        if (interfaceInfo == null) {
            return handleNoAuth(response);
        }

        // 检查是否还有调用次数
        boolean aBoolean = innerUserInterfaceInfoService.checkLeftNum(interfaceInfo.getId(), invokeUser.getId());
        if (!aBoolean) {
            return handleNoAuth(response);
        }

        // 添加用户信息和接口信息到染色
        String userId = String.valueOf(invokeUser.getId());
        String interfaceId = String.valueOf(interfaceInfo.getId());
//        TraceContext.setUserId(userId);
//        TraceContext.setInterfaceId(interfaceId);
        request = request.mutate()
                .header(USER_ID_HEADER, userId) // 添加用户ID到请求头
                .header(INTERFACE_ID_HEADER, interfaceId) // 添加接口ID到请求头
                .header(GATEWAY_HEADER, SignUtils.getGatewaySign(userId, interfaceId))
                .build();
        exchange = exchange.mutate().request(request).build();
        log.info("染色 - UserId: {} - InterfaceId: {}", userId, interfaceId);

        // 请求转发，调用模拟接口，响应日志
        // Mono<Void> filter = chain.filter(exchange); //todo chain.filter是异步操作，即还没执行完（模拟接口还没被调用），就走到了响应日志
        // return filter;
        // log.info("响应: " + response.getStatusCode());
        // log.info("custom global filter");
        return handleResponse(exchange, chain, interfaceInfo.getId(), invokeUser.getId());
    }

    @Override
    public int getOrder()
    {
        return -1;
    }

    /**
     * response增强,处理响应
     * gateway自定义网关响应处理器
     * @param exchange 路由交换机
     * @param chain 责任链
     * @return
     */
    public Mono<Void> handleResponse(ServerWebExchange exchange, GatewayFilterChain chain, long interfaceInfoId, long userId)
    {
        try {
            // 原有的response
            ServerHttpResponse originalResponse = exchange.getResponse();
            // 缓存区工厂
            DataBufferFactory bufferFactory = originalResponse.bufferFactory();
            // 拿到响应码
            HttpStatus statusCode = originalResponse.getStatusCode();
            if (statusCode != HttpStatus.OK) {
                return chain.filter(exchange); //降级处理返回数据
            }
            // ServerHttpResponseDecorator工具类装饰response,增强（装饰者设计模式）
            ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse)
            {
                // 等调用完转发的接口后才会执行writeWith
                @Override
                public Mono<Void> writeWith(Publisher<? extends DataBuffer> body)
                {
                    log.info("body instanceof Flux: {}", (body instanceof Flux));
                    if (body instanceof Flux) {
                        Flux<? extends DataBuffer> fluxBody = Flux.from(body); // fluxBody模拟接口的响应值
                        // 给返回结果写数据
                        // 拼接字符串
                        return super.writeWith(
                                fluxBody.map(dataBuffers -> {
                                    // 调用成功，接口调用次数+1 invokeCount
                                    try {
                                        innerUserInterfaceInfoService.invokeCount(interfaceInfoId, userId);
                                        //log.info("invokeCount执行：");
                                    }
                                    catch (Exception e) {
                                        log.info("invokeCount error", e);
                                    }
                                    byte[] content = new byte[dataBuffers.readableByteCount()];
                                    dataBuffers.read(content);
                                    DataBufferUtils.release(dataBuffers); //释放内存
                                    // 构建日志
                                    StringBuffer sb2 = new StringBuffer(200);
                                    sb2.append("<--- {} {} \n");
                                    List<Object> rspArgs = new ArrayList<>();
                                    rspArgs.add(originalResponse.getStatusCode());
                                    String data = new String(content, StandardCharsets.UTF_8);
                                    sb2.append(data);
                                    // 打印日志
                                    log.info("响应结果: {}", data);
                                    return bufferFactory.wrap(content);
                                }));
                    }
                    else {
                        // 调用失败，返回一个规范的错误码
                        log.error("<-- {} 响应code异常", getStatusCode());
                    }
                    return super.writeWith(body);
                }
            };
            // 设置response对象为装饰过的
            return chain.filter(exchange.mutate().response(decoratedResponse).build());
        }
        catch (Exception e) {
            log.error("网关处理响应异常{}", String.valueOf(e));
            return chain.filter(exchange);
        }
    }

    /**
     * Forbidden
     *
     * @param response
     * @return
     */
    public Mono<Void> handleNoAuth(ServerHttpResponse response)
    {
        log.info("未通过网关用户鉴权！！！");
        response.setStatusCode(HttpStatus.FORBIDDEN);
        return response.setComplete();
    }

    /**
     * Internal Server Error
     *
     * @param response
     * @return
     */
    public Mono<Void> handleInvokeError(ServerHttpResponse response)
    {
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        return response.setComplete();
    }


}