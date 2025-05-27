package com.wxy.api.gateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wxy.api.common.common.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.ConnectException;
import java.nio.charset.StandardCharsets;

@ControllerAdvice
@Order(-1) // 确保优先级高于默认的异常处理器
@Slf4j
public class GlobalExceptionHandler implements ErrorWebExceptionHandler
{

    private final ObjectMapper objectMapper = new ObjectMapper();

    @NotNull
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        // 自定义响应对象
        BaseResponse<String> response = new BaseResponse<>(500, null, "系统异常，请稍后重试");
        if (ex instanceof ConnectException) {
            response = new BaseResponse<>(500, null, "接口服务不可用，请稍后重试");
        }

        // 将响应对象转换为 JSON
        String responseBody;
        try {
            responseBody = objectMapper.writeValueAsString(response);
        } catch (JsonProcessingException e) {
            responseBody = "{\"code\": 500, \"message\": \"系统异常，请稍后重试\"}";
        }

        // 返回响应
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(responseBody.getBytes(StandardCharsets.UTF_8));
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

}
