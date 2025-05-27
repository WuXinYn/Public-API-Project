package com.wxy.api.gateway.nettyserver.component;

import com.wxy.api.gateway.nettyserver.NettyServer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class NettyServerRunner implements CommandLineRunner {

    private final NettyServer nettyServer;

    public NettyServerRunner(NettyServer nettyServer) {
        this.nettyServer = nettyServer;
    }

    @Override
    public void run(String... args) throws Exception {
        nettyServer.startServer(8100);
    }
}

