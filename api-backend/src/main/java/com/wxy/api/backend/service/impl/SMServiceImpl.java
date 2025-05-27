package com.wxy.api.backend.service.impl;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.wxy.api.backend.service.SMService;
import com.wxy.api.common.common.BusinessException;
import com.wxy.api.common.common.ErrorCode;
import com.wxy.api.common.utils.PhoneNumberUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class SMServiceImpl implements SMService
{

    @Value("${sms.sign-name}")
    private String signName; // 签名

    @Value("${sms.template-code}")
    private String templateCode; // 模板

    @Value("${sms.access-key}")
    private String accessKey; // ak

    @Value("${sms.secret-key}")
    private String secretKey; // sk

    @Value("${sms.region-id}")
    private String regionId;

    /**
     * 发送短信
     *
     * @param userAccount 收信人手机号
     * @param authCode     发送的验证码
     */
    public void sendMessage(String userAccount, int authCode)
    {
        DefaultProfile profile = DefaultProfile.getProfile(regionId, accessKey, secretKey);
        IAcsClient client = new DefaultAcsClient(profile);
        String param = String.valueOf(authCode);
        SendSmsRequest request = new SendSmsRequest();
        request.setSysRegionId(regionId);
        // 收信人手机号
        request.setPhoneNumbers(userAccount);
        // 申请的签名
        request.setSignName(signName);
        // 申请的模板
        request.setTemplateCode(templateCode);
        // 替换模板中的参数，必须为Json格式
        request.setTemplateParam("{\"code\":\"" + param + "\"}");
        try {
            // 获取发送结果
            SendSmsResponse response = client.getAcsResponse(request);
            String code = response.getCode();
            String message = response.getMessage();
            if (code != null && !"OK".equals(code)) {
                // 发送失败
                throw new BusinessException(ErrorCode.SEND_ERROR, message);
            }
            log.info("SMS response code: {}, msg:{}", response.getCode(), response.getMessage());
        }
        catch (ClientException e) {
            throw new BusinessException(ErrorCode.SEND_ERROR, e.getMessage());
        }
    }

    /**
     * 校验手机号是否合法
     *
     * @param userAccount 手机号
     */
    public void validatePhoneNumber(String userAccount)
    {
        if (userAccount == null || userAccount.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "手机号不能为空");
        }
        if (!PhoneNumberUtils.isValidPhoneNumber(userAccount)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "手机号格式错误");
        }
    }
}
