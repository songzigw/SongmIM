package cn.songm.im.server.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.songm.im.codec.IMException;
import cn.songm.im.codec.Protocol;
import cn.songm.im.codec.Protocol.Operation;
import cn.songm.im.server.handler.actioner.Actioner;
import cn.songm.im.server.handler.actioner.ActionerContainer;
import cn.songm.songmq.core.president.MQProtocol;
import io.netty.channel.ChannelHandlerContext;

/**
 * 业务消息分发处理器
 * 
 * @author zhangsong
 *
 */
@Component
public class ActionDispatcherHandler extends AbstractActioner {

    @Autowired
    private ActionerContainer actionerContainer;
    
    @Override
    protected void messageReceived(ChannelHandlerContext ctx, MQProtocol msg)
            throws Exception {
        Protocol pro = (Protocol) msg;
        if (!Operation.SEND_REQ.equals(pro.getOperation())) {
            ctx.fireChannelRead(msg);
            return;
        }
        
        checkSession(ctx.channel());
        Actioner ac = actionerContainer.find(pro.getOperation().getValue());
        if (ac == null) {
            log.warn("Not found operation: {}", pro.getOperation());
            return;
        }
        ac.action(ctx.channel(), pro, getSession(ctx.channel()));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        if (cause instanceof IMException) {
            closeConnection(ctx.channel());
            return;
        }
    }
    
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        log.debug("HandlerRemoved {}", ctx.channel());
    }
}
