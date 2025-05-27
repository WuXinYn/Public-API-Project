package com.api.apisensitiveword.service;

import com.api.apisensitiveword.filter.SensitiveWordFilter;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SensitiveFilterService {

    @Resource
    private SensitiveWordFilter sensitiveWordFilter;

    // 过滤敏感词
    public String filterText(String text) {
        return sensitiveWordFilter.filter(text);
    }
}
