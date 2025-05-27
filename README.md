# API-Project

#### 介绍
&emsp;基于 SpringBoot 和 React 的全栈微服务 API 开放平台面向普通用户和开发者，为其提供稳定的、风格统一的 RESTful API 接口调用，开发者也可以在平台上传自己
的接口供他人使用，这些接口通过平台调用更加方便、直观，并且平台提供标准的接口文档，方便各个阶段的开发者阅读理解使用，也降低上手难度。本系统的参与者主要
包括游客、用户和管理员。核心功能模块包括用户管理模块、接口管理模块、支付模块、订单管理模块、资源调配模块、通知管理模块、资源管理模块。

#### 软件架构
```
 后端：backend项目（api-backend）、开发者工具（api-client-sdk）、公共模块(api-common)、模拟接口(api-interface)、网关(api-gateway)  
 前端：开发者平台（官网 api-docs）、接口调用平台(api-frontend)
```

#### 环境配置
```
 前端：  
  React 18  
  AntDesignPro5.x脚手架  
  AntDesign&Procomponents组件库  
  Umi4前端框架  
  OpenAPI前端代码生成  
  Node 22.3.0  
 后端：  
  Java 21.0.6  
  Java Spring Boot  
  MySQL数据库 8.1.0  
  MyBatis-Plus及MyBatis X自动生成  
  API签名认证(Http调用)  
  Spring Boot Starter(SDK开发)  
  Dubbo分布式(RPC、Nacos)  
  Swagger+Knife4j接口文档生成  
  Spring Cloud Gateway微服务网关  
  Hutool、Apache Common Utils、Gson等工具库  
  Redis 5.0.14.1  
  Nacos 2.5.1  
 其他：  
  natapp  
```
 
#### 安装教程

 1. 先本地install安装api-common 模块  
 2. 然后本地install安装api-sdk 模块  
 3. 启动前端api-front和api-docs  
 4. 启动后端api-backend  
 5. 启动后端api-gateway  
 6. 启动模拟接口（api-interface模块中的三个模拟接口）  




#### 特技

