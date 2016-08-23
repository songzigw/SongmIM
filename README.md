# SongmIM
松美即时聊天通信服务器

## 概述

**松美IM**(SongmIM)为即时聊天通信服务器，可以为第三方应用提供立即可用的聊天服务，与第三方应用用户体系无缝集成。

可快速为社区微博、电子商务、企业应用集成即时消息服务。独立的消息服务器负责稳定的连接管理、消息路由和消息推送。

## 环境要求

JDK8

## 使用指南

以war包的形式放入Web容器下既可运行

## 开发指南

+ [Java后台服务开发API](https://github.com/songzigw/songm.im.backstage.java)
+ [WebIM客户端开发SDK](https://github.com/songzigw/songm.webim)
+ [WebIM客户端UI组件集成](https://github.com/songzigw/songm.webim.ui)
+ [Java语言客户端开发SDK](https://github.com/songzigw/songm.im.java)
+ Android客户端开发SDK (缺)

**后台开发API设计**

API接口如下：

名称 | 方法 | URL | 说明
--- | --- | --- | ---
获取通信令牌 | POST | /api/token | 为用户分配一个通信令牌，客户端通过令牌可以直接与消息服务器建立连接（Tcp、WebSocket、Long polling）

签名规则，每次请求接口时，均需要提供4个 HTTP Request Header，具体如下：

名称 | 类型 | 说明
--- | --- | ---
SM-Server-Key | String | 消息服务器的KEY
SM-Nonce | String | 随机数，无长度限制
SM-Timestamp | String | 时间戳，从1970年1月1日0点0分0秒开始到现在的毫秒数
SM-Signature | String | 数据签名

SM-Signature(数据签名)计算方法：将消息服务器的KEY、Nonce(随机数)、Timestamp(时间戳)三个字符串按先后顺序拼接成一个字符串并进行 SHA1 哈希计算。

+ **1.获取通信令牌**

请求URL
```
/api/token
```

请求参数

名称 | 类型 | 是否必须 | 描述
--- | --- | --- | ---
uid | string | true | 用户Uid
nick | string | true | 用户名称
avatar | string | false | 用户头像

返回结果


## 开发者

作者: 张松 ([zhangsong](mailto:songzigw@163.com)) 

版本: 0.1 (2016/8/21)

