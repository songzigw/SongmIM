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
package cn.songm.im.server.handler.codec;

import java.util.List;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import cn.songm.im.model.Protocol;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

/**
 * WebSocket通信协议的编码与解码
 * 
 * @author zhangsong
 *
 */
@ChannelHandler.Sharable
public class WsProtocolCodec
        extends MessageToMessageCodec<TextWebSocketFrame, Protocol> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Protocol pro,
            List<Object> list) throws Exception {
        JsonObject jObj = new JsonObject();

        jObj.addProperty("ver", pro.getVersion());
        jObj.addProperty("seq", pro.getSequence());
        jObj.addProperty("op", pro.getOperation());
        jObj.add("body", new JsonParser().parse(new String(pro.getBody())));

        list.add(new TextWebSocketFrame(jObj.toString()));
    }

    @Override
    protected void decode(ChannelHandlerContext ctx,
            TextWebSocketFrame textWebSocketFrame, List<Object> list)
            throws Exception {
        String text = textWebSocketFrame.text();
        JsonObject jObj = new JsonParser().parse(text).getAsJsonObject();

        Protocol pro = new Protocol();
        if (jObj.has("ver")) {
            pro.setVersion(jObj.get("ver").getAsShort());
        }
        if (jObj.has("seq")) {
            pro.setSequence(jObj.get("seq").getAsLong());
        }
        if (jObj.has("op")) {
            pro.setOperation(jObj.get("op").getAsInt());
        }
        if (jObj.has("body")) {
            pro.setBody(jObj.get("body").toString().getBytes());
        }
        list.add(pro);
    }
}
