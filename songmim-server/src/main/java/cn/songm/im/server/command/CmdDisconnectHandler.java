package cn.songm.im.server.command;

import org.springframework.stereotype.Component;

import cn.songm.im.server.command.codec.Protocol;
import cn.songm.im.server.command.codec.Protocol.Operation;
import cn.songm.songmq.core.president.MQProtocol;
import io.netty.channel.ChannelHandlerContext;

@Component
public class CmdDisconnectHandler extends AbstractHandler {

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, MQProtocol msg) throws Exception {
	Protocol pro = (Protocol) msg;
	if (!Operation.DISCONNECT.equals(pro.getOperation())) {
	    ctx.fireChannelRead(msg);
	    return;
	}
	log.debug("request: {}, {}", pro.getOperation(), ctx.channel());
	ctx.close().syncUninterruptibly();
    }

}
