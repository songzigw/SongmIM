package cn.songm.im.server.command.codec;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.songm.im.server.command.codec.Protocol.Operation;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

/**
 * 命令行，通信协议的编码解码
 * 
 * @author zhangsong
 *
 */
public class Codec extends MessageToMessageCodec<ByteBuf, Protocol> {

    private static final Logger LOG = LoggerFactory.getLogger(Codec.class);

    /**
     * 编码
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, Protocol msg, List<Object> out) throws Exception {
	msg.setLength();

	ByteBuf byteBuf = ByteBufAllocator.DEFAULT.buffer();
	byteBuf.writeInt(msg.getLength());
	byteBuf.writeLong(msg.getSequence());
	byteBuf.writeInt(msg.getOperation().getValue());
	if (msg.getBody() != null) {
	    byteBuf.writeBytes(msg.getBody());
	}

	out.add(byteBuf);
	LOG.debug("encode: {}", msg);
    }

    /**
     * 解码
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
	Protocol pro = new Protocol();
	pro.setLength(msg.readInt());
	pro.setSequence(msg.readLong());
	pro.setOperation(Operation.instance(msg.readInt()));

	if (pro.getLength() > Protocol.HEADER_FIXED) {
	    byte[] bs = new byte[pro.getLength() - Protocol.HEADER_FIXED];
	    msg.readBytes(bs);
	    pro.setBody(bs);
	}
	out.add(pro);
	LOG.debug("decode: {}", pro);
    }

}
