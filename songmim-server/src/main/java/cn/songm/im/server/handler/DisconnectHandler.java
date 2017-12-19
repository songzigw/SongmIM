package cn.songm.im.server.handler;

import org.springframework.stereotype.Component;

import cn.songm.im.codec.Protocol;
import cn.songm.im.codec.Protocol.Operation;
import cn.songm.songmq.core.president.MQProtocol;
import io.netty.channel.ChannelHandlerContext;

@Component
public class DisconnectHandler extends AbstractActioner {
    
    @Override
    protected void messageReceived(ChannelHandlerContext ctx, MQProtocol msg)
            throws Exception {
        Protocol pro = (Protocol) msg;
        if (!Operation.DISCONNECT.equals(pro.getOperation())) {
            ctx.fireChannelRead(msg);
            return;
        }
        log.debug("request: {}, {}", pro.getOperation(), ctx.channel());
        closeConnection(ctx.channel());
    }
    
}
