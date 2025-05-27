package com.wxy.api.backend.controller;

import com.wxy.api.backend.service.SMService;
import com.wxy.api.common.common.BaseResponse;
import com.wxy.api.common.common.BusinessException;
import com.wxy.api.common.common.ErrorCode;
import com.wxy.api.common.common.ResultUtils;
import com.wxy.api.common.model.enums.Limit;
import com.wxy.api.common.service.InnerRedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Random;

@RestController
@RequestMapping("/sms")
@Slf4j // 用作日志输出
public class SmsController
{
    @Resource
    private SMService smService;

    @DubboReference
    private InnerRedisService innerRedisService;

    @GetMapping("/getCode")
    public BaseResponse<String> phone(String userAccount)
    {
        smService.validatePhoneNumber(userAccount);

        // 发送前先看下我们是否已经缓存了验证码
        String yzm = innerRedisService.findParam(userAccount);

        if (yzm == null) {
            //生成六位数验证码
            int authNum = new Random().nextInt(899999) + 100000;
            smService.sendMessage(userAccount, authNum);
            String accountKey = Limit.CAPTCHA_CODE_KEY.getAddress() + userAccount;
            innerRedisService.setParams(accountKey, String.valueOf(authNum));
            return ResultUtils.success(ErrorCode.SEND_SUCCESS.getMessage());
        }
        else {
            // 存在，直接返回，不再发送验证码
            throw new BusinessException(ErrorCode.SEND_SUCCESS, "请勿重复发送验证码");
        }
    }
}
