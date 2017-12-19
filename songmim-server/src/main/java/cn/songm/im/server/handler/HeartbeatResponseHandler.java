package cn.songm.im.server.handler;

import org.springframework.stereotype.Component;

import cn.songm.im.codec.IMException;
import cn.songm.im.codec.Protocol;
import cn.songm.im.codec.Protocol.Operation;
import cn.songm.songmq.core.president.MQProtocol;
import io.netty.channel.ChannelHandlerContext;

@Component
public class HeartbeatResponseHandler extends AbstractActioner {
    
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
        // 检测在线状态
        checkSession(ctx.channel());
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
            closeConnection(ctx.channel());
            return;
        }
        super.exceptionCaught(ctx, cause);
    }
}
