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

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import songm.im.entity.Protocol;

/**
 * Tcp管道连接，通信协议的编码解码
 *
 * @author  zhangsong
 * @since   0.1, 2016-7-29
 * @version 0.1
 * 
 */
@Component
@ChannelHandler.Sharable
public class TcpProtocolCodec extends MessageToMessageCodec<ByteBuf, Protocol> {

    private static final Logger LOG = LoggerFactory
            .getLogger(TcpProtocolCodec.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, Protocol pro,
            List<Object> list) throws Exception {
        pro.setVersion(Protocol.VERSION);
        pro.setHeaderLen(Protocol.HEADER_LEN);

        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.buffer();
        byteBuf.writeShort(pro.getVersion());
        byteBuf.writeShort(pro.getHeaderLen());
        byteBuf.writeInt(pro.getOperation());
        byteBuf.writeLong(pro.getSequence());

        if (pro.getBody() != null) {
            pro.setPacketLen(Protocol.HEADER_LEN + pro.getBody().length);
            byteBuf.writeInt(pro.getPacketLen());
            byteBuf.writeBytes(pro.getBody());
        } else {
            pro.setPacketLen(Protocol.HEADER_LEN);
            byteBuf.writeInt(pro.getPacketLen());
        }

        list.add(byteBuf);

        LOG.debug("encode: {}", pro);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf,
            List<Object> list) throws Exception {
        Protocol pro = new Protocol();
        pro.setVersion(byteBuf.readShort());
        pro.setHeaderLen(byteBuf.readShort());
        pro.setOperation(byteBuf.readInt());
        pro.setSequence(byteBuf.readLong());
        pro.setPacketLen(byteBuf.readInt());

        if (pro.getPacketLen() > pro.getHeaderLen()) {
            byte[] bytes = new byte[pro.getPacketLen() - pro.getHeaderLen()];
            byteBuf.readBytes(bytes);
            pro.setBody(bytes);
        }

        list.add(pro);

        LOG.debug("decode: {}", pro);
    }
}
