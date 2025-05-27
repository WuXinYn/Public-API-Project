# API调用示例

本章节提供API开放平台各类接口的调用示例，帮助您更快地集成和使用API服务。

## 完整调用流程

下面是一个完整的API调用流程示例，包括认证、请求和响应处理。

### Java 完整示例

```java
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

public class ApiDemo {

    // 配置信息
    private static final String ACCESS_KEY = "您的Access Key";
    private static final String SECRET_KEY = "您的Secret Key";
    private static final String API_HOST = "http://localhost:7529";
    
    public static void main(String[] args) {
        try {
            // 示例1：获取接口列表
            String result1 = callApi("/api/interface/list", "GET", null);
            System.out.println("接口列表: " + result1);
            
            // 示例2：获取指定接口详情
            String result2 = callApi("/api/interface/get", "GET", "id=1");
            System.out.println("接口详情: " + result2);
            
            // 示例3：调用指定接口（假设是天气接口）
            String params = "{\"city\":\"北京\"}";
            String result3 = callApi("/api/interface/invoke", "POST", "id=1&userRequestParams=" + java.net.URLEncoder.encode(params, "UTF-8"));
            System.out.println("接口调用结果: " + result3);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 调用API接口
     *
     * @param path 接口路径
     * @param method 请求方法
     * @param params 请求参数
     * @return 接口响应结果
     */
    public static String callApi(String path, String method, String params) throws Exception {
        // 生成签名
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String nonce = generateNonce();
        String signStr = SECRET_KEY + timestamp + nonce + path;
        String sign = sha256(signStr);
        
        // 构建请求URL
        String url = API_HOST + path;
        if ("GET".equalsIgnoreCase(method) && params != null && !params.isEmpty()) {
            url += "?" + params;
        }
        
        // 创建HTTP连接
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod(method);
        conn.setRequestProperty("AccessKey", ACCESS_KEY);
        conn.setRequestProperty("Timestamp", timestamp);
        conn.setRequestProperty("Nonce", nonce);
        conn.setRequestProperty("Sign", sign);
        
        // 如果是POST请求，需要设置请求体
        if ("POST".equalsIgnoreCase(method) && params != null && !params.isEmpty()) {
            conn.setDoOutput(true);
            conn.getOutputStream().write(params.getBytes(StandardCharsets.UTF_8));
        }
        
        // 读取响应
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        
        return response.toString();
    }
    
    /**
     * 生成随机字符串
     */
    private static String generateNonce() {
        return String.valueOf(Math.random()).substring(2, 10);
    }
    
    /**
     * SHA-256哈希并Base64编码
     */
    private static String sha256(String input) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hash);
    }
}
```

### Python 完整示例

```python
import requests
import time
import hashlib
import base64
import random
import string
import json
import urllib.parse

# 配置信息
ACCESS_KEY = "您的Access Key"
SECRET_KEY = "您的Secret Key"
API_HOST = "http://localhost:7529"

def generate_nonce(length=8):
    """生成随机字符串"""
    return ''.join(random.choice(string.ascii_letters + string.digits) for _ in range(length))

def generate_signature(secret_key, timestamp, nonce, path):
    """生成签名"""
    sign_str = secret_key + timestamp + nonce + path
    hash_obj = hashlib.sha256(sign_str.encode('utf-8'))
    return base64.b64encode(hash_obj.digest()).decode('utf-8')

def call_api(path, method='GET', params=None, json_data=None):
    """调用API接口"""
    # 生成签名所需参数
    timestamp = str(int(time.time()))
    nonce = generate_nonce()
    signature = generate_signature(SECRET_KEY, timestamp, nonce, path)
    
    # 设置请求头
    headers = {
        'AccessKey': ACCESS_KEY,
        'Timestamp': timestamp,
        'Nonce': nonce,
        'Sign': signature
    }
    
    # 发送请求
    url = API_HOST + path
    if method.upper() == 'GET':
        response = requests.get(url, headers=headers, params=params)
    else:
        response = requests.post(url, headers=headers, params=params, json=json_data)
    
    return response.json()

if __name__ == "__main__":
    # 示例1：获取接口列表
    result1 = call_api("/api/interface/list")
    print(f"接口列表: {json.dumps(result1, indent=2, ensure_ascii=False)}")
    
    # 示例2：获取指定接口详情
    result2 = call_api("/api/interface/get", params={"id": 1})
    print(f"接口详情: {json.dumps(result2, indent=2, ensure_ascii=False)}")
    
    # 示例3：调用指定接口（假设是天气接口）
    params = {"id": 1}
    json_data = {"city": "北京"}
    result3 = call_api("/api/interface/invoke", method="POST", params=params, json_data=json_data)
    print(f"接口调用结果: {json.dumps(result3, indent=2, ensure_ascii=False)}")
```

## 常见API调用示例

### 用户登录

**Java 示例**:

```java
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class LoginExample {
    public static void main(String[] args) {
        try {
            // 构建登录请求数据
            String loginData = "userAccount=admin&userPassword=password123";
            
            // 创建URL和连接
            URL url = new URL("http://localhost:7529/api/user/login");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setDoOutput(true);
            
            // 发送请求
            try (OutputStream os = conn.getOutputStream()) {
                os.write(loginData.getBytes(StandardCharsets.UTF_8));
            }
            
            // 读取响应
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                System.out.println("登录响应: " + response.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

**Python 示例**:

```python
import requests

# 登录请求
login_data = {
    "userAccount": "admin",
    "userPassword": "password123"
}

response = requests.post("http://localhost:7529/api/user/login", data=login_data)
print(f"登录响应: {response.json()}")
```

### 接口调用

**Java 示例**:

```java
public class InvokeInterfaceExample {
    public static void main(String[] args) {
        try {
            // API调用参数
            String interfaceId = "1";
            String requestParams = "{\"param1\":\"value1\",\"param2\":\"value2\"}";
            String encodedParams = java.net.URLEncoder.encode(requestParams, "UTF-8");
            
            // 构建请求URL
            String apiUrl = "http://localhost:7529/api/interface/invoke?id=" + interfaceId 
                           + "&userRequestParams=" + encodedParams;
            
            // 创建连接和请求头
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            
            // 设置认证头
            conn.setRequestProperty("AccessKey", "您的Access Key");
            conn.setRequestProperty("Timestamp", String.valueOf(System.currentTimeMillis() / 1000));
            conn.setRequestProperty("Nonce", "随机字符串");
            conn.setRequestProperty("Sign", "签名");
            
            // 读取响应
            int responseCode = conn.getResponseCode();
            System.out.println("响应状态码: " + responseCode);
            
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            
            System.out.println("API响应: " + response.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

**Python 示例**:

```python
import requests
import time
import hashlib
import base64
import random
import string

# 配置信息
ACCESS_KEY = "您的Access Key"
SECRET_KEY = "您的Secret Key"

# 生成签名
def generate_signature(secret_key, timestamp, nonce, path):
    sign_str = secret_key + timestamp + nonce + path
    hash_obj = hashlib.sha256(sign_str.encode('utf-8'))
    return base64.b64encode(hash_obj.digest()).decode('utf-8')

# 生成随机字符串
def generate_nonce(length=8):
    return ''.join(random.choice(string.ascii_letters + string.digits) for _ in range(length))

# 接口调用参数
interface_id = 1
request_params = {"param1": "value1", "param2": "value2"}

# 请求路径
path = "/api/interface/invoke"

# 生成签名参数
timestamp = str(int(time.time()))
nonce = generate_nonce()
signature = generate_signature(SECRET_KEY, timestamp, nonce, path)

# 设置请求头
headers = {
    "AccessKey": ACCESS_KEY,
    "Timestamp": timestamp,
    "Nonce": nonce,
    "Sign": signature
}

# 发送请求
params = {"id": interface_id, "userRequestParams": json.dumps(request_params)}
response = requests.post(
    "http://localhost:7529" + path,
    headers=headers,
    params=params
)

print(f"API响应: {response.json()}")
```

## 错误处理

以下是处理API调用错误的示例:

```java
try {
    // 调用API...
    // 处理响应数据
    if (responseJson.getInt("code") != 0) {
        // 处理错误
        System.out.println("API调用失败: " + responseJson.getString("message"));
    } else {
        // 处理成功响应
        System.out.println("API调用成功!");
    }
} catch (Exception e) {
    // 处理网络错误或其他异常
    System.out.println("API调用异常: " + e.getMessage());
}
```

## 最佳实践

1. **错误重试**：对于网络错误或服务暂时不可用的情况，实现指数退避重试机制
2. **缓存结果**：对于不常变化的数据，合理缓存API响应结果
3. **并发控制**：控制并发请求数，避免触发频率限制
4. **超时设置**：设置合理的连接和读取超时时间
5. **日志记录**：记录API调用情况，方便排查问题 