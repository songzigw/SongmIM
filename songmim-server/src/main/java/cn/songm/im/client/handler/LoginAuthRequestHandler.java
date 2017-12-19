package cn.songm.im.client.handler;

import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

import com.google.gson.reflect.TypeToken;

import cn.songm.im.client.IMClient;
import cn.songm.im.codec.IMException.ErrorCode;
import cn.songm.im.codec.Protocol;
import cn.songm.im.codec.Protocol.Operation;
import cn.songm.im.codec.Result;
import cn.songm.im.codec.Session;
import cn.songm.songmq.core.CallbackInvoker;
import cn.songm.songmq.core.CallbackManager;
import cn.songm.songmq.core.president.AbstractMessageHandler;
import cn.songm.songmq.core.president.MQProtocol;
import cn.songm.songmq.core.util.JsonUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

public class LoginAuthRequestHandler extends AbstractMessageHandler {

    private final IMClient client;
    private boolean isConnected;
    
    public LoginAuthRequestHandler(IMClient client) {
        this.client = client;
    }
    
    public boolean isConnected() {
        return isConnected;
    }
    
    @Override
    protected void messageReceived(ChannelHandlerContext ctx, MQProtocol msg)
            throws Exception {
        // 获取授权结果信息
        Protocol p = (Protocol) msg;
        if (!Operation.LOGIN_RESP.equals(p.getOperation())) {
            ctx.fireChannelRead(msg);
            return;
        }
        log.debug("response: {}, {}", p.getOperation(), ctx.channel());
        Type type = new TypeToken<Result<Session>>() {}.getType();
        Result<Session> res = JsonUtils.getInstance().fromJson(p.getBody(), type);
        // 触发回调事件
        CallbackManager.getInstance().trigger(p.getSequence(), res);
        if (res.getErrorCode() != ErrorCode.OK.getCode()) {
            this.isConnected = true;
            ctx.close().syncUninterruptibly();
            return;
        }
        this.isConnected = true;
        ctx.fireChannelRead(msg);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // none
    }

    @SuppressWarnings("unchecked")
    public Result<Session> loginAuth(Channel ch) {
        Protocol p = new Protocol();
        p.setOperation(Operation.LOGIN_REQ);
        p.setBody(JsonUtils.getInstance().toJsonBytes(client.getSession()));
        // 定义事件
        CallbackInvoker invoker = new CallbackInvoker(p.getSequence());
        // 注册事件
        CallbackManager.getInstance().put(invoker);
        log.debug("request: {}, {}", p.getOperation(), ch);
        ch.writeAndFlush(p);
        return (Result<Session>) invoker.getResult(60 * 1000, TimeUnit.MILLISECONDS);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        log.debug(ctx.channel().toString());
        this.isConnected = false;
        if (client.getCallback() == null) return;
        client.getCallback().onDisconnected();
    }

}
