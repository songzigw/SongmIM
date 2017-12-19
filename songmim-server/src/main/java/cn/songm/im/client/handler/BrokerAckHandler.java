package cn.songm.im.client.handler;

import cn.songm.im.client.IMClient;
import cn.songm.im.codec.IMException.ErrorCode;
import cn.songm.im.codec.Protocol;
import cn.songm.im.codec.Protocol.Operation;
import cn.songm.im.codec.Result;
import cn.songm.im.codec.model.Message;
import cn.songm.songmq.core.president.AbstractMessageHandler;
import cn.songm.songmq.core.president.MQProtocol;
import cn.songm.songmq.core.util.JsonUtils;
import io.netty.channel.ChannelHandlerContext;

public class BrokerAckHandler extends AbstractMessageHandler {

    private IMClient client;
    
    public BrokerAckHandler(IMClient client) {
        this.client = client;
    }
    
    @Override
    protected void messageReceived(ChannelHandlerContext ctx, MQProtocol msg)
            throws Exception {
        Protocol p = (Protocol) msg;
        if (!Operation.BROKER.equals(p.getOperation())) {
            ctx.fireChannelRead(msg);
            return;
        }
        Message message = JsonUtils.getInstance().fromJson(p.getBody(), Message.class);
        log.debug("request: {}, {}", p.getOperation(), ctx.channel());
        // 消息处理
        if (client.getCallback() != null) {
            client.getCallback().onMessage(message);
        }
        Protocol resp = buildMessage(p, ErrorCode.OK, null, message);
        ctx.writeAndFlush(resp);
        log.debug("response: {}, {}", resp.getOperation(), ctx.channel());
    }

    private Protocol buildMessage(Protocol req, ErrorCode errCode, String errMsg, Message message) {
        Protocol pro = new Protocol();
        pro.setOperation(Operation.BROACK);
        pro.setSequence(req.getSequence());
        Result<Message> result = new Result<>();
        result.setErrorCode(errCode.getCode());
        result.setMessage(errMsg);
        result.setData(message);
        pro.setBody(JsonUtils.getInstance().toJsonBytes(result));
        return pro;
    }

}
