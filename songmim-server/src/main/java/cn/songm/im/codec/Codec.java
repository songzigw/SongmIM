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
package cn.songm.im.codec;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.songm.im.codec.Protocol.Operation;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

/**
 * Tcp管道连接，通信协议的编码解码
 *
 * @author zhangsong
 * @since 0.1, 2016-7-29
 * @version 0.1
 * 
 */
@ChannelHandler.Sharable
public class Codec extends MessageToMessageCodec<ByteBuf, Protocol> {

    private static final Logger LOG = LoggerFactory.getLogger(Codec.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, Protocol pro,
            List<Object> list) throws Exception {
        if (pro.getBody() != null) {
            pro.setLength(Protocol.HEADER_FIXED + pro.getBody().length);
        } else {
            pro.setLength(Protocol.HEADER_FIXED);
        }

        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.buffer();
        byteBuf.writeByte(pro.getVersion().getLevel());
        byteBuf.writeInt(pro.getLength());
        byteBuf.writeLong(pro.getSequence());
        byteBuf.writeInt(pro.getOperation().getValue());

        if (pro.getBody() != null) {
            byteBuf.writeBytes(pro.getBody());
        }

        list.add(byteBuf);
        LOG.debug("encode: {}", pro);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf,
            List<Object> list) throws Exception {
        Protocol pro = new Protocol();
        pro.setVersion(Version.instance(byteBuf.readByte()));
        pro.setLength(byteBuf.readInt());
        pro.setSequence(byteBuf.readLong());
        pro.setOperation(Operation.instance(byteBuf.readInt()));

        if (pro.getLength() > Protocol.HEADER_FIXED) {
            byte[] bytes = new byte[pro.getLength() - Protocol.HEADER_FIXED];
            byteBuf.readBytes(bytes);
            pro.setBody(bytes);
        }

        list.add(pro);
        LOG.debug("decode: {}", pro);
    }
}
