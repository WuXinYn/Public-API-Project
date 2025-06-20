package com.api.apisensitiveword;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class ApiSensitiveWordApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiSensitiveWordApplication.class, args);
    }

}
