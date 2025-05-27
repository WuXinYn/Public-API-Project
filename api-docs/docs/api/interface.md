# 接口详情

本页面提供API开放平台每个接口的详细说明，包括请求参数、响应格式和注意事项。

## 用户类接口

### 用户登录

**接口路径**: `/api/user/login`

**请求方法**: POST

**接口说明**: 用户登录接口，登录成功后返回用户信息

**请求参数**:

| 参数名 | 类型 | 是否必须 | 说明 |
|-------|------|---------|------|
| userAccount | string | 是 | 用户账号 |
| userPassword | string | 是 | 用户密码 |

**响应结果**:

```json
{
  "code": 0,
  "data": {
    "id": 1,
    "userAccount": "admin",
    "userName": "管理员",
    "userAvatar": "https://example.com/avatar.png",
    "userRole": "admin",
    "createTime": "2023-01-01T00:00:00Z",
    "updateTime": "2023-01-01T00:00:00Z"
  },
  "message": "success"
}
```

### 用户注册

**接口路径**: `/api/user/register`

**请求方法**: POST

**接口说明**: 用户注册接口，注册成功后返回用户ID

**请求参数**:

| 参数名 | 类型 | 是否必须 | 说明 |
|-------|------|---------|------|
| userAccount | string | 是 | 用户账号 |
| userPassword | string | 是 | 用户密码 |
| checkPassword | string | 是 | 确认密码 |

**响应结果**:

```json
{
  "code": 0,
  "data": 123456789, // 用户ID
  "message": "success"
}
```

### 获取当前用户

**接口路径**: `/api/user/get/login`

**请求方法**: GET

**接口说明**: 获取当前登录用户信息

**请求参数**: 无

**响应结果**:

```json
{
  "code": 0,
  "data": {
    "id": 1,
    "userAccount": "admin",
    "userName": "管理员",
    "userAvatar": "https://example.com/avatar.png",
    "userRole": "admin",
    "createTime": "2023-01-01T00:00:00Z",
    "updateTime": "2023-01-01T00:00:00Z"
  },
  "message": "success"
}
```

## 接口类接口

### 获取接口列表

**接口路径**: `/api/interface/list`

**请求方法**: GET

**接口说明**: 获取所有可用接口列表

**请求参数**:

| 参数名 | 类型 | 是否必须 | 说明 |
|-------|------|---------|------|
| current | number | 否 | 当前页码，默认为1 |
| pageSize | number | 否 | 每页数量，默认为10 |
| name | string | 否 | 接口名称，用于搜索 |
| description | string | 否 | 接口描述，用于搜索 |
| method | string | 否 | 请求方法，用于筛选 |
| status | number | 否 | 接口状态，0-关闭，1-开启 |

**响应结果**:

```json
{
  "code": 0,
  "data": [
    {
      "id": 1,
      "name": "获取天气信息",
      "description": "根据城市名称获取实时天气信息",
      "url": "/api/weather",
      "method": "GET",
      "requestHeader": "{'Content-Type': 'application/json'}",
      "responseHeader": "{'Content-Type': 'application/json'}",
      "status": 1,
      "userID": 1,
      "createTime": "2023-01-01T00:00:00Z",
      "updateTime": "2023-01-01T00:00:00Z"
    }
  ],
  "message": "success"
}
```

### 获取接口详情

**接口路径**: `/api/interface/get`

**请求方法**: GET

**接口说明**: 获取指定接口的详细信息

**请求参数**:

| 参数名 | 类型 | 是否必须 | 说明 |
|-------|------|---------|------|
| id | number | 是 | 接口ID |

**响应结果**:

```json
{
  "code": 0,
  "data": {
    "id": 1,
    "name": "获取天气信息",
    "description": "根据城市名称获取实时天气信息",
    "url": "/api/weather",
    "method": "GET",
    "requestHeader": "{'Content-Type': 'application/json'}",
    "responseHeader": "{'Content-Type': 'application/json'}",
    "status": 1,
    "userID": 1,
    "createTime": "2023-01-01T00:00:00Z",
    "updateTime": "2023-01-01T00:00:00Z"
  },
  "message": "success"
}
```

### 调用接口

**接口路径**: `/api/interface/invoke`

**请求方法**: POST

**接口说明**: 调用指定接口，需要传入接口ID和调用参数

**请求参数**:

| 参数名 | 类型 | 是否必须 | 说明 |
|-------|------|---------|------|
| id | number | 是 | 接口ID |
| userRequestParams | string | 否 | 调用参数，JSON格式 |

**响应结果**:

接口调用成功后的响应结果根据实际调用的接口而定，以下是一个示例：

```json
{
  "code": 0,
  "data": {
    "weather": "晴",
    "temperature": "26°C",
    "humidity": "65%",
    "wind": "东北风3级",
    "updateTime": "2023-06-01 14:30:00"
  },
  "message": "success"
}
```

## 统计类接口

### 查看调用统计

**接口路径**: `/api/userInterfaceInfo/get`

**请求方法**: GET

**接口说明**: 获取用户的接口调用统计信息

**请求参数**:

| 参数名 | 类型 | 是否必须 | 说明 |
|-------|------|---------|------|
| userId | number | 否 | 用户ID，不传则查询当前登录用户 |
| interfaceInfoId | number | 否 | 接口ID，不传则查询所有接口 |

**响应结果**:

```json
{
  "code": 0,
  "data": {
    "id": 1,
    "userId": 1,
    "interfaceInfoId": 1,
    "totalNum": 100,
    "leftNum": 900,
    "status": 1,
    "createTime": "2023-01-01T00:00:00Z",
    "updateTime": "2023-01-01T00:00:00Z"
  },
  "message": "success"
}
```

## 注意事项

1. 所有接口都需要进行[认证与鉴权](/guide/authentication.html)，除非特别说明
2. 请求参数中如包含JSON格式的字符串，需要进行URL编码
3. 接口调用频率限制为每分钟100次，超出将返回状态码42900
4. 接口返回的时间格式均为ISO 8601标准（如：2023-06-01T14:30:00Z） 