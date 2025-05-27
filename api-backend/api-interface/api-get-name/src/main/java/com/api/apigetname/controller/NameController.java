package com.api.apigetname.controller;

import com.api.apigetname.modle.User;
import com.wxy.api.common.common.BusinessException;
import com.wxy.api.common.common.ErrorCode;
import com.wxy.api.sdk.utils.GatewayHeaderUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import static com.wxy.api.common.constant.TraceCheckConstant.*;

@RestController
@RequestMapping("/name")
@Slf4j
public class NameController
{
    @GetMapping("/get")
    public String getNameByGet(HttpServletRequest request){
        GatewayHeaderUtils.validateGatewayHeaders(request);
        return "Get你的名字是" + "test";
    }

    @PostMapping("/post")
    public String getNameByPost(@RequestParam String name){
        return "Post你的名字是" + name;
    }

    @PostMapping("/user")
    public String getUserNameByPost(@RequestBody User user, HttpServletRequest request){
        GatewayHeaderUtils.validateGatewayHeaders(request);
        return "Post用户名是" + user.getUsername();
    }
}
