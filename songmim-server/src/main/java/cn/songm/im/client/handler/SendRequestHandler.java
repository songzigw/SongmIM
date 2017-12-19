package cn.songm.im.client.handler;

import java.lang.reflect.Type;

import com.google.gson.reflect.TypeToken;

import cn.songm.im.codec.AckListener;
import cn.songm.im.codec.IMException.ErrorCode;
import cn.songm.im.codec.Protocol;
import cn.songm.im.codec.Protocol.Operation;
import cn.songm.im.codec.Result;
import cn.songm.im.codec.model.Message;
import cn.songm.songmq.core.CallbackInvoker;
import cn.songm.songmq.core.CallbackListener;
import cn.songm.songmq.core.CallbackManager;
import cn.songm.songmq.core.president.AbstractMessageHandler;
import cn.songm.songmq.core.president.MQProtocol;
import cn.songm.songmq.core.util.JsonUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

public class SendRequestHandler extends AbstractMessageHandler {

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, MQProtocol msg)
            throws Exception {
        Protocol p = (Protocol) msg;
        if (!Operation.SEND_RESP.equals(p.getOperation())) {
            ctx.fireChannelRead(msg);
            return;
        }
        log.debug("response: {}, {}", p.getOperation(), ctx.channel());
        Type type = new TypeToken<Result<Message>>() {}.getType();
        Result<Message> res = JsonUtils.getInstance().fromJson(p.getBody(), type);
        // 触发回调事件
        CallbackManager.getInstance().trigger(p.getSequence(), res);
    }

    public void buildMessage(Channel ch, Message message, AckListener<Message> listener) {
        Protocol p = new Protocol();
        p.setOperation(Operation.SEND_REQ);
        p.setBody(JsonUtils.getInstance().toJsonBytes(message));
        // 定义事件
        CallbackInvoker invoker = new CallbackInvoker(p.getSequence());
        // 事件监听器
        invoker.join(new CallbackListener() {
            @Override
            public void onCallBack(Object o) {
                @SuppressWarnings("unchecked")
                Result<Message> t = (Result<Message>) o;
                if (t.getErrorCode() != ErrorCode.OK.getCode()) {
                    listener.onError(t.getErrorCode(), t.getMessage());
                    return;
                }
                listener.onSuccess(t.getData());
            }
        });
        // 注册事件
        CallbackManager.getInstance().put(invoker);
        log.debug("request: {}, {}", p.getOperation(), ch);
        ch.writeAndFlush(p);
    }
}
