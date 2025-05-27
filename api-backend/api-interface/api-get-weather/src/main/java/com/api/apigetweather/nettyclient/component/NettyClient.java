package com.api.apigetweather.nettyclient.component;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

@Component
public class NettyClient {

    @Resource
    private HeartbeatManager heartbeatManager;

    public void start(int localPort, String remoteHost, int remotePort) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ch.pipeline().addLast(new HeartbeatClientHandler());
                            ch.pipeline().addLast(new StringDecoder(StandardCharsets.UTF_8)); // 解码器
                            ch.pipeline().addLast(new StringEncoder(StandardCharsets.UTF_8)); // 编码器
                        }
                    });

            // 允许端口快速重用
            bootstrap.option(ChannelOption.SO_REUSEADDR, true);

            // 绑定本地端口
            bootstrap.localAddress(new InetSocketAddress(0)); // 动态分配本地端口

            // 连接远程服务器
            ChannelFuture future = bootstrap.connect(remoteHost, remotePort).sync();
            Channel channel = future.channel();

            // 启动心跳任务
            heartbeatManager.startHeartbeat(localPort, channel);

            // 等待关闭
            channel.closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }
}

