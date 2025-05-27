package com.api.apigetweather.nettyclient.component;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class HeartbeatManager
{

    /**
     * todo 自定义请求IP
     */
    public static final String ip = "localhost";

    /**
     * todo 自定义请求路径
     */
    public static final String path = "/api/weather/city";
    /**
     * todo 自定义请求端口
     */
    public static final int post = 8126;

    /**
     * todo 自定义请求方式
     */
    public static final String method = "POST";

    private final ConcurrentHashMap<Integer, ScheduledExecutorService> schedulerMap = new ConcurrentHashMap<>();

    public void startHeartbeat(int localPort, Channel channel)
    {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            if (channel != null && channel.isActive()) {
                String msg = "发送心跳包 from the host = http://" + ip + ":" + post + path + "\\" + method;
                log.info(msg);
                channel.writeAndFlush(msg);
            }
            else {
                System.out.println("Channel for port " + localPort + " is inactive. Stopping heartbeat.");
                stopHeartbeat(localPort);
            }
        }, 1, 60, TimeUnit.MINUTES); // 每5分钟发送一次

        schedulerMap.put(localPort, scheduler);
    }

    public void stopHeartbeat(int localPort)
    {
        ScheduledExecutorService scheduler = schedulerMap.remove(localPort);
        if (scheduler != null) {
            scheduler.shutdown();
        }
    }
}

