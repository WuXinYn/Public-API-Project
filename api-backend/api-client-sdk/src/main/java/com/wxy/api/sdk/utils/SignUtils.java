package com.wxy.api.sdk.utils;

import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;

import static com.wxy.api.common.constant.TraceCheckConstant.GateWay_Ans;

/**
 * 签名工具
 */
public class SignUtils {

    /**
     * 生成签名
     * @param body 用户请求参数
     * @param secretKey 密钥
     * @return 签名
     */
    public static String getSign(String body, String secretKey) {
        Digester md5 = new Digester(DigestAlgorithm.SHA256); // 选择加密算法
        String content = body + "." + secretKey;
        String digestHex = md5.digestHex(content);
        return digestHex;
    }

    /**
     * 生成网关签名
     * @param userId
     * @param interfaceId
     * @return
     */
    public static String getGatewaySign(String userId, String interfaceId) {
        Digester md5 = new Digester(DigestAlgorithm.SHA256); // 选择加密算法
        String content = userId + "::invoke::" + interfaceId + "::from::" + GateWay_Ans;
        String digestHex = md5.digestHex(content);
        return digestHex;
    }
}
