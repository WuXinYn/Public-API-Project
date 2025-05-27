package com.api.apisensitiveword.config;

import com.api.apisensitiveword.filter.SensitiveWordFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SensitiveWordConfig {

    @Bean
    public SensitiveWordFilter sensitiveWordFilter() {
        SensitiveWordFilter filter = new SensitiveWordFilter();
        // 加载敏感词库（可以从文件或数据库读取）
        filter.addWord("傻逼");
        filter.addWord("你妈");
        filter.addWord("草");
        filter.addWord("草尼玛");
        filter.addWord("我靠");
        filter.addWord("靠");
        filter.addWord("fuck");
        filter.addWord("你妈逼");
        return filter;
    }
}
