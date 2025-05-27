package com.api.apisensitiveword.controller;

import com.api.apisensitiveword.service.SensitiveFilterService;
import com.wxy.api.sdk.utils.GatewayHeaderUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/filter")
public class SensitiveFilterController {

    @Resource
    private SensitiveFilterService sensitiveFilterService;

    // 过滤敏感词接口
    @PostMapping("/sensitive")
    public String filterSensitiveWords(@RequestBody String text, HttpServletRequest request) {
        GatewayHeaderUtils.validateGatewayHeaders(request);
        return sensitiveFilterService.filterText(text);
    }
}
