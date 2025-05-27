# SDK 使用指南

本文档将介绍如何获取、安装和使用API开放平台的SDK。

## SDK下载

### Java SDK

- [点击下载api-接口案例.rar](https://pan.baidu.com/s/1DPRcj9A8I2ES_i-JVvOOmg?pwd=aj5j)
- [在 GitHub 查看示例源码](https://github.com/WuXinYn/API-SDK.git)


## 快速开始

### 1. 配置密钥与依赖

在使用SDK之前，您需要先在平台获取 `accessKey` 和 `secretKey`。

```dependency
<dependency>
   <groupId>com.wxy.api</groupId>
   <artifactId>api-client-sdk</artifactId>
   <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### 2. 初始化客户端

```java
import com.wxy.apiclientsdk.client.ApiClient;

String accessKey = "您的accessKey";
String secretKey = "您的secretKey";

ApiClient client = new ApiClient(accessKey, secretKey);
```

### 3. 调用接口

```java
// 示例：
String result = tempClient.handle(stringBaseRequire, method, nonce);
if (result == null) {
    throw new BusinessException(ErrorCode.Request_ERROR, "返回数据为null");
}
```

## 接口列表

SDK 目前支持以下请求方式：

| 接口名称 | 方法名    | 请求方式 | 描述  |
|------|--------|----------|-----|
| XXX  | getXXX | GET | XXX |
| XXX  | getXXX | POST | XXX |

## 错误处理

SDK 会抛出自定义异常：

```java
try {
    String result = tempClient.handle(stringBaseRequire, method, nonce);
} 
catch (BusinessException e) {
    log.error("调用失败 - Code:{}\n Message:{}\n", e.getCode(), e.getMessage());
}
```

## 示例代码

### 完整调用示例

```java
import com.wxy.apiclientsdk.client.ApiClient;
import com.wxy.apiclientsdk.model.User;

public class ApiClientExample {
    
    public static void main(String[] args) {
        // 1. 配置密钥
        String accessKey = "您的accessKey";
        String secretKey = "您的secretKey";
        
        // 2. 创建客户端实例
        ApiClient client = new ApiClient(accessKey, secretKey);
        
        try {
            String url = "http://localhost:8080...";    // TODO 自定义请求地址
            String userRequestParams = "...";           // TODO 自定义请求参数
            JSONObject userParams = JSON.parseObject(userRequestParams, JSONObject.class);
            BaseRequire<JSONObject> stringBaseRequire = new BaseRequire<>(url, userParams);
            String nonce = RandomUtil.randomNumbers(8); // 随机数，防止代理重放
            
            // 3. 调用接口
            String method1 = "GET";
            String result = tempClient.handle(stringBaseRequire, method1, nonce);
            
            // 4. Post方式调用
            String method2 = "POST";
            String result = tempClient.handle(stringBaseRequire, method2, nonce);
        } 
        catch (BusinessException e) {
            log.error("调用失败 - Code:{}\n Message:{}\n", e.getCode(), e.getMessage());
        }
    }
}
```

## 注意事项

1. SDK实例线程安全，建议复用
2. 请妥善保管密钥，不要泄露
3. 如遇调用失败，请查看错误信息进行处理


## sdk工具库介绍

```text
工具库提供了一些便捷的方法，如：
1. 自定义访问对象工具类：CusAccessObjectUtil
2. **<span style="color:red">网关染色校验</span>**：GatewayHeaderUtils
3. IP地址转换：IPConversion
PS: 网关染色校验工具类是提供给需要在平台发布自己接口的开发者使用
使用案例如下所示：
```
```java
@GetMapping("/get")
public String getNameByGet(HttpServletRequest request){
    // 网关染色校验工具类使用
    GatewayHeaderUtils.validateGatewayHeaders(request); 
    return "SUCCESS";
}
```
   
## 常见问题

### 1. 签名错误

- 检查 accessKey 和 secretKey 是否正确
- 确保时间戳为毫秒级
- 验证随机字符串长度为8位

### 2. 调用次数超限

- 检查接口的调用次数限制
- 合理控制调用频率
- 如需提升限制，请联系管理员

### 3. 连接超时

- 检查网络连接
- 适当增加超时时间
- 实现请求重试机制

## 更新日志

### v1.0.0 (2024-03-08)

- 首次发布
- 支持基础的用户信息获取接口
- 实现了GET和POST两种请求方式 
