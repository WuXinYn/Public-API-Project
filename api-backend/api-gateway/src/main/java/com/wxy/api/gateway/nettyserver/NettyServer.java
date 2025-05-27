package com.wxy.api.gateway.nettyserver;

import com.wxy.api.gateway.nettyserver.handler.ExceptionHandlingHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;

@Component
public class NettyServer {

    @Resource
    private ExceptionHandlingHandler exceptionHandlingHandler;

    public void startServer(int port) throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new StringDecoder(StandardCharsets.UTF_8)); // 解码器
                            pipeline.addLast(new StringEncoder(StandardCharsets.UTF_8)); // 编码器
                            pipeline.addLast(new IdleStateHandler(15, 20, 15)); // 心跳检测：未读、未写、未读写
                            pipeline.addLast(exceptionHandlingHandler); // 添加共享的处理器
                        }
                    });

            System.out.println("Netty server starting on port " + port);
            bootstrap.bind(port).sync().channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
