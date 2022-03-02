# naraka cloud

 一个spring cloud alibaba 脚手架

## 主要包含以下技术栈 中间件 第三方包

`Sentinel` `Nacos` `Spring cloud gateway` `Spring Cloud Feign`  `Redis` `Mybatis`

| Spring Cloud Alibaba Version | Spring Cloud Version | Spring Boot Version  | Sentinel Version  | Nacos Version  |
|:----------------------------:|:--------------------:|:--------------------:|:-----------------:|:--------------:|
|          2021.0.1.0          |       2021.0.1       |        2.6.3         |       1.8.3       |     1.4.2      |

## 组件

**[naraka-cloud-common](#)**: naraka通用的 `DTO`,`enum`,`annotation`

**[naraka-cloud-boot-starter](#)**: naraka的starter,提供数据脱敏,加解密,字段根据权限序列化,过滤, mybatis
plus的通用接口查询方法和新增,修改通用字段自动填充,redis的统一配置

**[naraka-cloud-gateway](#)**: 统一网关,处理鉴权,路由,负载均衡,熔断等

**[naraka-cloud-admin](#)**: 提供系统账号创建,权限分配,授权,角色增删改查接口





