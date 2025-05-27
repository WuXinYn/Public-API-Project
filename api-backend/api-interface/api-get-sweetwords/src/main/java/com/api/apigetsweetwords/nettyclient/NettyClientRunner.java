package com.api.apigetsweetwords.nettyclient;

import com.api.apigetsweetwords.nettyclient.component.HeartbeatManager;
import com.api.apigetsweetwords.nettyclient.component.NettyClient;
import com.api.apigetsweetwords.nettyclient.pojo.NettyPort;
import org.springframework.boot.CommandLineRunner;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class NettyClientRunner implements CommandLineRunner
{
    @Resource
    private NettyClient nettyClient;

    @Override
    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 2000)) // 最多尝试 3 次，每次重试之间有 2 秒的延迟
    public void run(String... args) throws Exception {
        // 启动客户端绑定不同本地端口
        new Thread(() -> {
            try {
                int localPost = HeartbeatManager.post;
                nettyClient.start(localPost, NettyPort.Gateway_Netty_Server_Host.getContent(), NettyPort.Gateway_Netty_Server_Port.getValue()); // 自定义客户端
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
