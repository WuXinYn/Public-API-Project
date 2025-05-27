# 快速开始

本文将指导您如何快速开始使用API开放平台的服务，从注册账号到完成第一次API调用。

## 步骤1：注册账号

访问[API开放平台注册页面](http://localhost:8000/api/user/register)，填写注册信息完成账号注册。

::: warning 注意
- 用户账号长度不小于4位
- 密码长度不小于8位
- 账号和密码仅支持字母和数字
:::

## 步骤2：登录平台

注册成功后，使用您的账号和密码[登录平台](http://localhost:8000/api/user/login)。

## 步骤3：获取API密钥

登录成功后，进入个人中心页面，在"密钥管理"标签页中可以看到您的API密钥信息：

- **Access Key**：用于标识API调用者身份
- **Secret Key**：用于加密签名字符串和服务器验证签名字符串

::: warning 注意
- 请妥善保管您的Secret Key，不要泄露给他人
- 如果发现密钥泄露，请立即在平台更换新的密钥
- 建议定期更换密钥以提高安全性
:::

## 步骤4：了解接口规范

在开始调用接口之前，请先了解以下重要信息：

### 请求限制
- 每个用户每个接口每天最多可调用 100 次
- 单个 IP 每秒最多可发起 10 次请求
- 超出限制将返回 429 状态码

### 通用请求头
| 参数名 | 类型 | 是否必须 | 说明 |
|-------|------|---------|------|
| accessKey | string | 是 | 访问密钥 |
| timestamp | string | 是 | 当前时间戳（毫秒） |
| nonce | string | 是 | 随机字符串，长度8位 |
| sign | string | 是 | 请求签名 |

### 通用响应格式
```json
{
    "code": 0,       // 状态码，0表示成功
    "data": {},      // 响应数据
    "message": "ok"  // 状态描述
}
```

### 错误码说明
| 错误码 | 说明 |
|-------|------|
| 0 | 成功 |
| 40000 | 请求参数错误 |
| 40100 | 未登录 |
| 40101 | 无权限 |
| 40400 | 请求数据不存在 |
| 40300 | 禁止访问 |
| 50000 | 系统内部异常 |

## 步骤5：使用SDK（推荐）

我们提供了SDK来简化API的调用过程，强烈推荐使用SDK进行开发。

### 获取SDK

您可以通过以下方式获取SDK：

1. [直接下载 JAR 包](http://localhost:8000/sdk/java/api-client-sdk-0.0.1.jar)
2. [访问 SDK 文档](/tools/sdk.html)了解更多信息
3. [在 GitHub 查看源码](https://github.com/wxy/api-client-sdk)

### Maven依赖（Java）
```xml
<dependency>
    <groupId>com.wxy</groupId>
    <artifactId>api-client-sdk</artifactId>
    <version>0.0.1</version>
</dependency>
```

### SDK使用示例（Java）
```java
import com.wxy.apiclientsdk.client.ApiClient;
import com.wxy.apiclientsdk.model.User;

public class ApiClientExample {
    public static void main(String[] args) {
        String accessKey = "你的accessKey";
        String secretKey = "你的secretKey";
        
        ApiClient client = new ApiClient(accessKey, secretKey);
        User user = client.getNameByGet("wxy");
        System.out.println(user);
    }
}
```

::: tip 提示
查看 [SDK 使用指南](/tools/sdk.html) 了解更多用法和示例。
:::

## 步骤6：调用测试

在接口详情页面，您可以直接进行接口调用测试：

1. 选择要测试的接口
2. 填写必要的请求参数
3. 点击"调用"按钮
4. 查看调用结果

::: tip 建议
在正式环境使用前，建议先在测试环境充分测试。
:::

## 步骤7：错误处理

在调用API时，可能遇到以下常见错误：

| 错误码 | 说明 | 解决方案 |
|-------|------|---------|
| 40100 | 未登录 | 检查登录状态 |
| 40101 | 无权限 | 检查调用权限和签名是否正确 |
| 40300 | 禁止访问 | 检查账号状态和调用权限 |
| 40400 | 请求数据不存在 | 检查请求参数是否正确 |
| 40000 | 请求参数错误 | 检查参数格式和值是否符合要求 |
| 50000 | 系统内部异常 | 请联系技术支持 |

## 最佳实践

1. **错误重试**
   - 建议对可重试的错误进行指数退避重试
   - 推荐最大重试3次

2. **请求签名**
   - 每次请求使用不同的nonce
   - 确保时间戳为最新
   - 签名算法：通过 `accessKey` + `secretKey` + `timestamp` + `nonce` 生成

3. **性能优化**
   - 复用SDK客户端实例
   - 合理设置超时时间
   - 必要时使用缓存

4. **安全建议**
   - 使用HTTPS调用API
   - 定期更换密钥
   - 不要在客户端暴露secretKey

## 下一步

现在您已经了解了如何基本使用API开放平台，接下来可以：

- 了解[认证与鉴权](/guide/authentication.html)机制
- 查看完整的[API参考文档](/api/)
- 探索更多[使用示例](/api/examples.html)
- 阅读[SDK使用指南](/tools/sdk.html)
- 了解[心跳包工具](/tools/heartbeat.html)

::: tip 获取帮助
如果您在使用过程中遇到问题：
1. 查看[常见问题](/guide/faq.html)
2. 在GitHub提交Issue
3. 联系技术支持：admin@example.com
::: 