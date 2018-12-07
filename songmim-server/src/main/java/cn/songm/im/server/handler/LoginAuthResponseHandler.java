package cn.songm.im.server.handler;

import org.springframework.stereotype.Component;

import cn.songm.im.codec.IMException;
import cn.songm.im.codec.IMException.ErrorCode;
import cn.songm.im.codec.Protocol;
import cn.songm.im.codec.Protocol.Operation;
import cn.songm.im.codec.Result;
import cn.songm.im.codec.Session;
import cn.songm.songmq.core.president.MQProtocol;
import cn.songm.songmq.core.util.JsonUtils;
import io.netty.channel.ChannelHandlerContext;

@Component
public class LoginAuthResponseHandler extends AbstractActioner {

    private Protocol buildMessage(Protocol req, ErrorCode errCode, String message, Session session) {
        Protocol pro = new Protocol();
        pro.setOperation(Operation.LOGIN_RESP);
        pro.setSequence(req.getSequence());
        Result<Session> result = new Result<>();
        result.setErrCode(errCode.getCode());
        result.setErrDesc(message);
        result.setData(session);
        pro.setBody(JsonUtils.getInstance().toJsonBytes(result));
        return pro;
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, MQProtocol msg)
            throws Exception {
        Protocol pro = (Protocol) msg;
        if (!Operation.LOGIN_REQ.equals(pro.getOperation())) {
            ctx.fireChannelRead(msg);
            return;
        }
        Session session = JsonUtils.getInstance().fromJson(pro.getBody(),
                Session.class);
        log.debug("request: {}, {}", pro.getOperation(), ctx.channel());
        Protocol resp;
        try {
            // 登入授权成功
            session = authService.online(session.getTokenId(),
                    session.getUid(), ctx.channel());
            this.saveSession(ctx.channel(), session);
            resp = buildMessage(pro, ErrorCode.OK, null, session);
            ctx.writeAndFlush(resp);
        } catch (IMException e) {
            // 登入授权失败
            resp = buildMessage(pro, e.getErrorCode(), e.getErrorDesc(), null);
            ctx.writeAndFlush(resp);
            // 关闭连接
            ctx.close().syncUninterruptibly();
        }
        log.debug("response: {}, {}", resp.getOperation(), ctx.channel());
    }

}
