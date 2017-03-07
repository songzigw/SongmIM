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
package cn.songm.im.handler.codec;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import cn.songm.im.model.Protocol;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

/**
 * WebSocket通信协议的编码与解码
 * @author zhangsong
 *
 */
@ChannelHandler.Sharable
public class WsProtocolCodec extends
        MessageToMessageCodec<TextWebSocketFrame, Protocol> {

    private final ObjectMapper jsonMapper = new ObjectMapper();

    @Override
    protected void encode(ChannelHandlerContext ctx, Protocol pro,
            List<Object> list) throws Exception {
        ObjectNode root = jsonMapper.createObjectNode();
        JsonNode body = jsonMapper.readTree(pro.getBody());

        root.put("ver", pro.getVersion());
        root.put("seq", pro.getSequence());
        root.put("op", pro.getOperation());
        root.set("body", body);
        
        list.add(new TextWebSocketFrame(root.toString()));
    }

    @Override
    protected void decode(ChannelHandlerContext ctx,
            TextWebSocketFrame textWebSocketFrame, List<Object> list)
            throws Exception {
        String text = textWebSocketFrame.text();
        JsonNode root = jsonMapper.readTree(text);

        Protocol pro = new Protocol();
        if (root.has("ver")) {
            pro.setVersion(root.get("ver").shortValue());
        }
        if (root.has("seq")) {
            pro.setSequence(root.get("seq").asLong());
        }
        if (root.has("op")) {
            pro.setOperation(root.get("op").asInt());
        }
        if (root.has("body")) {
            pro.setBody(root.get("body").toString().getBytes());
        }
        list.add(pro);
    }
}
