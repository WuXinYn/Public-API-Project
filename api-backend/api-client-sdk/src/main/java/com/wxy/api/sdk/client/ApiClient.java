package com.wxy.api.sdk.client;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wxy.api.common.common.BusinessException;
import com.wxy.api.common.common.ErrorCode;
import com.wxy.api.common.model.enums.HeaderNameType;
import com.wxy.api.sdk.model.BaseRequire;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static com.wxy.api.sdk.utils.SignUtils.getSign;

/**
 * 调用第三方接口的客户端
 */
@Slf4j
public class ApiClient
{

    private static final String GATEWAY_HOST = "http://localhost:8090";
    private String accessKey;
    private String secretKey;

    public ApiClient()
    {
    }

    public ApiClient(String accessKey, String secretKey)
    {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    /**
     * 获取请求头
     * @param json 请求参数
     * @param nonce
     * @param host
     * @return
     */
    public Map<String, String> getHeaderMap(String json, String nonce, String host)
    {
        Map<String, String> hashMap = new HashMap<>();
        // ak
        hashMap.put(HeaderNameType.Access_Key.getHeaderName(), accessKey);
        // 随机数，防止代理重放
        hashMap.put(HeaderNameType.Nonce.getHeaderName(), nonce);
        // 用户的请求参数
        hashMap.put(HeaderNameType.Temp_Body.getHeaderName(), json);
        // 时间戳, 系统当前时间除以1000
        hashMap.put(HeaderNameType.Timestamp.getHeaderName(), String.valueOf(System.currentTimeMillis() / 1000));
        // 原接口host
        hashMap.put(HeaderNameType.Interface_Host.getHeaderName(), host);
        // 签名
        hashMap.put(HeaderNameType.Sign.getHeaderName(), getSign(json, secretKey)); // hashMap.put("secretKey",secretKey); // 一定不能发送给后端，防止请求被拦截
        return hashMap;
    }

    /**
     * 处理请求
     * @param stringBaseRequire
     * @param method
     * @param nonce
     * @return
     */
    public String handle(BaseRequire<JSONObject> stringBaseRequire, String method, String nonce)
    {
        String result = "";
        String ip = stringBaseRequire.getIp();
        int index = StringUtils.ordinalIndexOf(ip,"/",3);
        String host = ip.substring(0,index);
        String path = ip.substring(index);
        String url = GATEWAY_HOST + path;
        stringBaseRequire.setIp(host);
        String json = JSON.toJSONString(stringBaseRequire.getParams());
        String chineseHeaderValue = URLEncoder.encode(json, StandardCharsets.UTF_8);
        HttpRequest request;
        if (method.equals("GET")) {
            request = HttpRequest.get(url)
                    .header("Content-Type", "application/json; charset=UTF-8") // 设置请求头
                    .addHeaders(getHeaderMap(json, nonce, host))
                    .charset("UTF-8");
            result = request.execute().body();
        }else if (method.equals("POST")) {
            request = HttpRequest.post(url)
                    .header("Content-Type", "application/json; charset=UTF-8") // 设置请求头
                    .addHeaders(getHeaderMap(json, nonce, host)) // 设置请求头
                    .header(HeaderNameType.Body.getHeaderName(), chineseHeaderValue)
                    .body(json) // 设置请求体
                    .charset("UTF-8"); // 确保字符集为 UTF-8

            result = request.execute().body();
        }
        return result;
    }

}
