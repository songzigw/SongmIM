package cn.songm.im.server.handler;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.songm.common.beans.Result;
import cn.songm.common.utils.JsonUtils;
import cn.songm.im.business.IMException;
import cn.songm.im.business.service.SessionService;
import cn.songm.im.model.Protocol;
import cn.songm.songmq.core.president.AbstractMsgHandler;
import cn.songm.songmq.core.president.MQProtocol;
import io.netty.channel.ChannelHandlerContext;

/**
 * 消息分发处理器
 * 
 * @author zhangsong
 *
 */
@Component
public class MessageDispatcher extends AbstractMsgHandler {

    private static final Logger LOG = LoggerFactory
            .getLogger(MessageDispatcher.class);

    @Autowired
    private HandlerManager operationManager;
    @Resource(name = "sessionService")
    private SessionService sessionService;

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, MQProtocol msg)
            throws Exception {
        Protocol pro = (Protocol) msg;
        Handler op = operationManager.find(pro.getOperation());
        if (op != null) {
            try {
                op.action(ctx.channel(), pro);
            } catch (IMException e) {
                Result<Object> res = new Result<Object>();
                res.setErrorCode(e.getErrorCode().name());
                res.setErrorDesc(e.getDescription());
                pro.setBody(JsonUtils.getInstance().toJsonBytes(res));
                ctx.writeAndFlush(pro);
                // ctx.close().syncUninterruptibly();
            }
        } else {
            LOG.warn("Not found operation: {}", pro.getOperation());
        }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        String sesId = ctx.channel().attr(Constants.KEY_SESSION_ID).get();
        sessionService.removeChannel(sesId, ctx.channel());
        LOG.debug("HandlerRemoved {}", ctx.channel());
    }
}
