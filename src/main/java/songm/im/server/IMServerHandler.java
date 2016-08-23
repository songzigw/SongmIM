package songm.im.server;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import songm.im.entity.Protocol;
import songm.im.operation.IMOperation;
import songm.im.operation.Operation;
import songm.im.service.AuthService;

@Component
@ChannelHandler.Sharable
public class IMServerHandler extends SimpleChannelInboundHandler<Protocol> {

    private static final Logger LOG = LoggerFactory
            .getLogger(IMServerHandler.class);

    @Autowired
    private IMOperation imOperation;
    @Autowired
    private AuthService authService;

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Protocol pro)
            throws Exception {
        LOG.debug("MessaageReceived", pro.toString());
        Operation op = imOperation.find(pro.getOperation());
        if (op != null) {
            op.action(ctx.channel(), pro);
        } else {
            LOG.warn("Not found operation: " + pro.getOperation());
        }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        LOG.debug("HandlerRemoved", ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        LOG.error("ExceptionCaught", cause);
    }
}
