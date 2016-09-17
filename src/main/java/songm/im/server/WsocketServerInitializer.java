/*
 * Copyright [2016] [zhangsong <songm.cn>].
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package songm.im.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * WebSocket管道初始化
 *
 * @author zhangsong
 * @since 0.1, 2016-8-9
 * @version 0.1
 * 
 */
@Component
public class WsocketServerInitializer extends ChannelInitializer<NioSocketChannel> {

    @Autowired
    private WsocketProtocolCodec protocolCodec;
    @Autowired
    private IMServerHandler serverHandler;

    protected void initChannel(NioSocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        // 编解码 http 请求
        pipeline.addLast(new HttpServerCodec());
        // 写文件内容
        pipeline.addLast(new ChunkedWriteHandler());
        // 聚合解码 HttpRequest/HttpContent/LastHttpContent 到 FullHttpRequest
        // 保证接收的 Http 请求的完整性
        pipeline.addLast(new HttpObjectAggregator(64 * 1024));
        // 处理其他的 WebSocketFrame
        pipeline.addLast(new WebSocketServerProtocolHandler("/im"));
        // 处理 TextWebSocketFrame
        pipeline.addLast(protocolCodec);
        pipeline.addLast(serverHandler);
    }

}
