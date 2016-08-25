# SongmIM
松美即时聊天消息服务器

## 概述

**松美IM**(SongmIM)为即时聊天消息服务器，可以为第三方应用提供立即可用的聊天服务，与第三方应用用户体系无缝集成。

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

### 后台开发API设计

API接口如下：

名称 | 方法 | URL | 说明
--- | --- | --- | ---
获取通信令牌 | POST | /api/token | 为用户分配一个通信令牌，客户端通过令牌可以直接与消息服务器建立连接（Tcp、WebSocket、Long polling）

签名规则，每次请求接口时，均需要提供4个 HTTP Request Header，具体如下：

名称 | 类型 | 说明
--- | --- | ---
SM-Server-Key | String | 消息服务器的KEY
SM-Nonce | String | 随机数，无长度限制
SM-Timestamp | String | 时间戳，以毫秒为单位
SM-Signature | String | 数据签名，算法见下一行

SM-Signature(数据签名)算法：将消息服务器的Secret、Nonce(随机数)、Timestamp(时间戳)三个字符串按先后顺序拼接成一个字符串并进行 SHA1 哈希计算。


**API接口详解**

1、*获取通信令牌* `/api/token`

请求参数

名称 | 类型 | 是否必须 | 描述
--- | --- | --- | ---
uid | string | true | 用户Uid
nick | string | true | 用户名称
avatar | string | false | 用户头像

返回结果
```
略
```

2、*获取历史聊天记录* `/api/history`

请求参数

名称 | 类型 | 是否必须 | 描述
--- | --- | --- | ---

返回结果
```
略
```

### 客户端与服务端Tcp通信协议封装

协议分为两部分，数据包头和包体

| 包头 | 包体 |
| --- | --- |
| 描述数据包整体信息，占整个数据包的20个字节(Byte) | 以Json格式封装了实际数据 |

----- 包头部分 -----

- Version
占2byte，描述协议版本号

- HeaderLen
占2byte，描述包头的长度

- Operation
占4byte，描述具体操作项，告诉程序数据的处理行为

- Sequence
占8byte，每次操作的唯一标识，每次操作生成一个唯一的序列码

- PacketLen
占4byte，描述整个数据包的字节大小（固定包头20byte + 包体实际字节）

----- 包体部分 -----

- Body
占用字节，以实际字节长度为准，以Json格式封装了实际数据


**请求操作项详解**

1、*连接并授权*

----- 请求数据包 -----
```
Operation = 1
Body = {}
```

----- 响应数据包 -----
```
Operation = 1
Body = {}
```

2、*发送消息*

----- 请求数据包 -----
```
Operation = 2
Body = {}
```

----- 响应数据包 -----
```
Operation = 2
Body = {}
```

## License

```
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

## 开发者

作者: 张松 ([zhangsong](mailto:songzigw@163.com)) 

版本: 0.1 (2016/8/21)

