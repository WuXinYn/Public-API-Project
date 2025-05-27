package com.api.apigetweather;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableRetry // 启用重试支持
@Slf4j
public class ApiGetWeatherApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGetWeatherApplication.class, args);
    }

}
