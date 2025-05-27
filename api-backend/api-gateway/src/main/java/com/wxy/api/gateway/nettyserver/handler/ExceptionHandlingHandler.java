package com.wxy.api.gateway.nettyserver.handler;

import com.alibaba.nacos.shaded.com.google.common.util.concurrent.RateLimiter;
import com.wxy.api.gateway.nettyserver.service.ExceptionService;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 自定义的心跳处理器
 */
@Component
@ChannelHandler.Sharable
public class ExceptionHandlingHandler extends ChannelInboundHandlerAdapter {

    @Resource
    private ExceptionService exceptionService;

    private static final Logger logger = LoggerFactory.getLogger(ExceptionHandlingHandler.class);

    // 创建一个固定大小的线程池
    private static final ExecutorService heartbeatExecutor = Executors.newFixedThreadPool(10);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
    {
        // 正常消息处理逻辑
        String message = (String) msg;
        if (message.contains("心跳包")){
            String channelID = String.valueOf(ctx.channel().id());

            // 提交到线程池处理
            heartbeatExecutor.submit(() -> {
                logger.info("收到来自 {} 的心跳包: {}, 发送心跳响应...", channelID, message);
                exceptionService.setMsg(channelID, message); // 记录接口信息
                ctx.writeAndFlush("心跳响应"); // 服务端响应
            });
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // 异步处理异常
        String channelID = String.valueOf(ctx.channel().id());
        exceptionService.handleException(cause, channelID);
        // ctx.close(); // 关闭连接（可选）
    }
}
