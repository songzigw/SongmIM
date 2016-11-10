# SongmIM
松美即时聊天消息服务器（Songm IM message server）

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
获取历史聊天记录 | POST | /api/history | 聊天历史记录

签名规则，每次请求接口时，均需要提供4个 HTTP Request Header，具体如下：

名称 | 类型 | 说明
--- | --- | ---
SM-Server-Key | String | 消息服务器的KEY
SM-Nonce | String | 随机数，无长度限制
SM-Timestamp | String | 时间戳，以毫秒为单位
SM-Signature | String | 数据签名，算法见说明

*PS:*

SM-Signature(数据签名)算法：将消息服务器的Secret、Nonce(随机数)、Timestamp(时间戳)三个字符串按先后顺序拼接成一个字符串并进行 SHA1 哈希计算。


**API接口详解**

1、*获取通信令牌* `/api/token`

请求参数

名称 | 类型 | 是否必须 | 描述
--- | --- | --- | ---
uid | string | true | 用户Uid
nick | string | true | 用户名称
avatar | string | false | 用户头像

返回成功：

```json
{
    "succeed": true,            // 获取Token成功
    "data"   : {
        "tokenId": <<TokenId>>, // 消息服务分的配通信令牌
        "uid"    : <<uid>>,
        "nick"   : <<nick>>,
        "avatar" : <<avatar>>
    }
}
```

返回失败：

```json
{
    "succeed"  : false,    // 获取Token失败
    "errorCode": <<错误码>> // 返回失败的原因
}
```

2、*获取历史聊天记录* `/api/history`

请求参数

名称 | 类型 | 是否必须 | 描述
--- | --- | --- | ---
uid | string | ture | 用户Uid
oid | string | ture | 聊天对象
point | long | false | 时间点之前的聊天记录（时间戳）
number | int | false | 获取记录的条数

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

- PacketLen
占4byte，描述整个数据包的字节大小（固定包头20byte + 包体实际字节）

- Sequence
占8byte，每次操作的唯一标识，每次操作生成一个唯一的序列码

- Operation
占4byte，描述具体操作项，告诉程序数据的处理行为

----- 包体部分 -----

- Body
占用字节，以实际字节长度为准，以Json格式封装了实际数据


**请求操作项详解**

1、*连接并授权*

----- 请求数据包 -----

数据项 | 数据值 | 描述
--- | --- | ---
version | 1 | 版本号
headerLen | 20 | 包头字节大小
packetLen | 20 + 包体长度 | 整个包的字节大小
sequence | 当前时间戳 | 数据包序列
***operation*** | 1 | 请求连接并授权
body | Session数据 | 举例如下：

```json
{
    "sessionId": <<SessionID>>, // 当前客户端保存的会话，如果没有，值为null
    "tokenId"  : <<TokenId>>    // 第三方应用，请求消息服务器获取的通信令牌
}
```

----- 响应数据包 -----

数据项 | 数据值 | 描述
--- | --- | ---
version | 1 | 版本号
headerLen | 20 | 包头字节大小
packetLen | 20 + 包体长度 | 整个包的字节大小
sequence | 当前时间戳 | 数据包序列
***operation*** | 1 | 响应连接并授权的结果
body | Result对象 | 举例如下：

返回成功：

```json
{
    "succeed"  : true,          // 连接服务器并且授权访问客户端访问成功
    "sessionId": <<SessionID>>, // 返回服务器分配的会话ID
    "tokenId"  : <<TokenId>>    // 返回之前传递通信令牌
}
```

返回失败：

```json
{
    "succeed"  : false,     // 连接服务器并且授权失败
    "errorCode": <<错误码>>  // 返回失败的原因
}
```

2、*发送聊天消息*

----- 请求数据包 -----

数据项 | 数据值 | 描述
--- | --- | ---
version | 1 | 版本号
headerLen | 20 | 包头字节大小
packetLen | 20 + 包体长度 | 整个包的字节大小
sequence | 当前时间戳 | 数据包序列
***operation*** | 2 | 请求发送聊天消息
body | Message对象 | 举例如下：

```json
{
    "sessionId": <<SessionID>>, // 当前客户端保存的会话，如果没有，id: null
    "tokenId"  : <<TokenId>>    // 第三方应用，请求消息服务器获取的通信令牌
}
```

----- 响应数据包 -----

数据项 | 数据值 | 描述
--- | --- | ---
version | 1 | 版本号
headerLen | 20 | 包头字节大小
packetLen | 20 + 包体长度 | 整个包的字节大小
sequence | 当前时间戳 | 数据包序列
***operation*** | 2 | 响应聊天消息处理结果
body | Result对象 | 举例如下：

返回成功：

```json
{
    "succeed": true // 聊天消息发送成功
}
```

返回失败：

```json
{
    "succeed"  : false,     // 聊天消息发送失败
    "errorCode": <<错误码>>  // 返回失败的原因
}
```

3、*接收消息*

----- 接收数据包 -----

数据项 | 数据值 | 描述
--- | --- | ---
version | 1 | 版本号
headerLen | 20 | 包头字节大小
packetLen | 20 + 包体长度 | 整个包的字节大小
sequence | 0 | 数据包序列
***operation*** | 3  | 服务端发送数据到客户端
body | Result对象 | 举例如下：

```json
{
    "succeed": true, // 返回成功，接收到的消息都是成功的没有失败
    "created": <<产生的时间戳>>,
    "from"   : <<发送者>>,
    "to"     : <<接收者>>,
    "body"   : <<聊天内容>>
}
```

### 长轮询接口设计

*url* `/polling/long`

请求参数

名称 | 类型 | 是否必须 | 描述
--- | --- | --- | ---
token | string | true | 服务器分配的通信令牌
session | string | false | 连接成功后分配的Session
chId | string | false | 连接成功后分配的管道ID

返回成功（第一次轮询，返回Session数据）：

```json
{
    "succeed": true,
    "data"   : {
        "sessionId": <<sessionId>>,
        "attribute": {"ch_id": <<chId>>},
        "tokenId"  : <<TokenId>>,
        "uid"      : <<uid>>
    }
}
```

返回成功（以后每次轮询，返回Message数据）：

```json
{
    "succeed": true,
    "data"   : {
        
    }
}
```

返回失败（返回连接失败）：

```json
{
    "succeed"  : false,    // 连接失败
    "errorCode": <<错误码>> // 失败的原因
}
```

### 消息发送接口设计

*url* `/polling/message`

请求参数

名称 | 类型 | 是否必须 | 描述
--- | --- | --- | ---
session | string | true | 连接成功后分配的Session
chId | string | true | 连接成功后分配的管道ID
from | string | true | 消息发送者
to | string | true | 消息接收者
text | string | true | 消息内容

返回成功：

```json
{
    "succeed": true
}
```

返回失败：

```json
{
    "succeed"  : false,
    "errorCode": <<错误码>>
}
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

