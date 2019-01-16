package cn.songm.im.server.command;

import cn.songm.im.codec.IMException;
import cn.songm.im.server.command.codec.Protocol;
import cn.songm.im.server.command.codec.Protocol.Operation;
import cn.songm.songmq.core.president.MQProtocol;
import io.netty.channel.ChannelHandlerContext;

public class CmdHeartbeatHandler extends AbstractHandler {
    
    @Override
    protected void messageReceived(ChannelHandlerContext ctx, MQProtocol msg)
            throws Exception {
        Protocol pro = (Protocol) msg;
        if (!Operation.PING.equals(pro.getOperation())) {
            ctx.fireChannelRead(msg);
            return;
        }
        log.debug("request: {}, {}", pro.getOperation(), ctx.channel());
        Protocol resp = buildMessage(pro);
        ctx.writeAndFlush(resp);
        log.debug("response: {}, {}", resp.getOperation(), ctx.channel());
    }
    
    private Protocol buildMessage(Protocol req) {
        Protocol pro = new Protocol();
        pro.setOperation(Operation.PONG);
        pro.setSequence(req.getSequence());
        return pro;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        if (cause instanceof IMException) {
            ctx.close().syncUninterruptibly();
            return;
        }
        super.exceptionCaught(ctx, cause);
    }
}
