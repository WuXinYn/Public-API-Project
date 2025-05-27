package com.wxy.api.sdk;

import com.wxy.api.sdk.client.ApiClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * API客户端配置类
 * 负责配置API客户端所需的属性和Bean
 * 通过Spring配置自动注入accessKey和secretKey
 */
@Configuration // 标记为Spring配置类
@ConfigurationProperties("api.client") // 从配置文件中读取以api.client为前缀的配置项，用于用户引入sdk后的自定义配置客户端
@Data // Lombok注解，自动生成getter、setter等方法
@ComponentScan  // 自动扫描当前包及其子包中的组件
public class ApiClientConfig
{
    /**
     * API访问密钥，用于身份验证
     */
    private String accessKey;

    /**
     * API密钥，用于签名生成
     */
    private String secretKey;

    /**
     * 创建并配置ApiClient实例
     * @return 配置好的ApiClient实例
     */
    @Bean
    public ApiClient apiClient() {
        return new ApiClient(accessKey, secretKey);
    }
}
