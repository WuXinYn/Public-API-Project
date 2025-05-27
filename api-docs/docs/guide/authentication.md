# 认证与鉴权

API开放平台采用基于密钥的认证方式，确保API调用的安全性。本文将详细介绍认证与鉴权的原理和实现方法。

## 认证机制原理

我们的认证机制基于以下几个关键要素：

1. **Access Key**: 用于标识API调用者的身份
2. **Secret Key**: 用于生成签名的密钥，仅调用者和服务端知晓
3. **签名算法**: 使用简单的字符串拼接算法生成请求签名
4. **时间戳**: 请求的发起时间，用于防止重放攻击
5. **随机字符串**: 每次请求都不同，增加签名的唯一性

## 获取API密钥

登录平台后，在个人中心的"密钥管理"页面可以查看您的API密钥：

- **Access Key**: 您的API访问标识
- **Secret Key**: 您的API密钥（请妥善保管，不要泄露）

## 签名生成步骤

每次调用API时，需要按照以下步骤生成签名：

1. 获取当前时间戳（毫秒级）
2. 生成一个随机字符串（Nonce）
3. 构造签名字符串：将accessKey、secretKey、timestamp、nonce按顺序拼接
4. 使用拼接后的字符串作为签名

### 签名生成示例（Java）

```java
public class SignGenerator {
    public static String getSign(String accessKey, String secretKey) {
        // 获取毫秒级时间戳
        String timestamp = String.valueOf(System.currentTimeMillis());
        // 生成随机串
        String nonce = RandomUtil.randomNumbers(8);
        // 生成签名
        return accessKey + secretKey + timestamp + nonce;
    }
}
```

### 签名生成示例（Python）

```python
import time
import random
import string

def generate_sign(access_key, secret_key):
    # 获取毫秒级时间戳
    timestamp = str(int(time.time() * 1000))
    # 生成随机串
    nonce = ''.join(random.choices(string.digits, k=8))
    # 生成签名
    return access_key + secret_key + timestamp + nonce
```

## 请求格式

在调用API时，需要在HTTP请求头中添加以下参数：

| 参数名 | 说明 |
|-------|------|
| accessKey | 您的Access Key |
| timestamp | 当前时间戳（毫秒级） |
| nonce | 随机字符串，长度8位 |
| sign | 根据上述算法生成的签名 |

### 请求示例

```http
GET /api/name/user?name=wxy HTTP/1.1
Host: localhost:8090
accessKey: acc-12345678
timestamp: 1678234567000
nonce: 12345678
sign: acc-12345678sec-12345678167823456700012345678
```

## 签名验证流程

服务端在收到请求后，会进行以下验证：

1. 检查请求头是否包含所有必需参数
2. 检查accessKey是否存在且有效
3. 检查用户是否有调用权限
4. 使用相同的算法生成签名，并与请求中的签名进行比对
5. 检查接口调用次数是否超出限制

只有通过所有验证的请求才会被处理，否则会返回相应的错误码。

## 常见问题

### 签名验证失败的原因

- 参数缺失或不正确
- Access Key或Secret Key错误
- 签名生成算法不正确
- 时间戳格式错误

### 如何处理签名验证失败

1. 检查所有请求参数是否正确
2. 确保使用的是毫秒级时间戳
3. 检查签名生成代码是否正确
4. 确保Access Key和Secret Key正确

## 安全建议

为了保障您的API调用安全，建议：

1. 妥善保管Secret Key，不要在客户端代码中硬编码
2. 定期检查API调用日志，发现异常及时处理
3. 使用HTTPS进行API调用，防止信息被窃听
4. 在服务端进行签名验证时增加适当的时间窗口检查

## 示例代码

### 使用SDK调用接口

```java
import com.wxy.apiclientsdk.client.ApiClient;
import com.wxy.apiclientsdk.model.User;

public class ApiClientExample {
    public static void main(String[] args) {
        String accessKey = "您的accessKey";
        String secretKey = "您的secretKey";
        
        ApiClient client = new ApiClient(accessKey, secretKey);
        User user = client.getNameByGet("wxy");
        System.out.println(user);
    }
}
```

## 错误码说明

| 错误码 | 说明 | 处理方法 |
|-------|------|---------|
| 40100 | 未登录 | 检查登录状态 |
| 40101 | 无权限 | 检查调用权限和签名 |
| 40300 | 禁止访问 | 检查账号状态 |
| 40000 | 请求参数错误 | 检查参数格式 |
| 50000 | 系统内部异常 | 联系技术支持 | 