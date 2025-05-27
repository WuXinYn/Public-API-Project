package com.wxy.api.backend;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@MapperScan("com.wxy.api.backend.mapper")
@EnableDubbo
@EnableAsync
@EnableCaching
public class ApiBackendApplication
{

    public static void main(String[] args)
    {
        SpringApplication.run(ApiBackendApplication.class, args);
    }

}
