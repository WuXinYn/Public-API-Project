package com.wxy.api.backend.service;

import org.springframework.stereotype.Service;

@Service
public interface SMService
{
    /**
     * 发送短信
     *
     * @param phoneNumbers 收信人手机号
     * @param param        发送的验证码
     */
    void sendMessage(String phoneNumbers, int param);

    /**
     * 校验手机号是否合法
     *
     * @param phoneNumber 手机号
     */
    void validatePhoneNumber(String phoneNumber);
}
