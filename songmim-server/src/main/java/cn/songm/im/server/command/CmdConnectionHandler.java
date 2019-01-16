package cn.songm.im.server.command;

import java.net.InetSocketAddress;

import cn.songm.im.server.command.codec.Protocol;
import cn.songm.im.server.command.codec.Protocol.Operation;
import cn.songm.songmq.core.president.MQProtocol;
import io.netty.channel.ChannelHandlerContext;

public class CmdConnectionHandler extends AbstractHandler {

    private Protocol buildMessage(Protocol req, String flag) {
        Protocol pro = new Protocol();
        pro.setOperation(Operation.CONNECT_ACK);
        pro.setSequence(req.getSequence());
        pro.setBody(flag.getBytes());
        return pro;
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, MQProtocol msg)
            throws Exception {
        Protocol pro = (Protocol) msg;
        if (!Operation.CONNECT_REQ.equals(pro.getOperation())) {
            ctx.fireChannelRead(msg);
            return;
        }
        // String body = new String(pro.getBody());
        
        log.debug("request: {}, {}", pro.getOperation(), ctx.channel());
        InetSocketAddress local = (InetSocketAddress) ctx.channel().localAddress();
        InetSocketAddress remot = (InetSocketAddress) ctx.channel().remoteAddress();
        Protocol resp;
        if (local.getAddress().getHostAddress().equals(remot.getAddress().getHostAddress())) {
            // 登入授权成功
            resp = buildMessage(pro, "succeed");
            ctx.writeAndFlush(resp);
        } else {
            // 登入授权失败
            resp = buildMessage(pro, "failure");
            ctx.writeAndFlush(resp);
            // 关闭连接
            ctx.close().syncUninterruptibly();
        }
        
        log.debug("response: {}, {}", resp.getOperation(), ctx.channel());
    }

}