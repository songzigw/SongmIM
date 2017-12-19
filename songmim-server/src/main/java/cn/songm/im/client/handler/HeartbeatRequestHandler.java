package cn.songm.im.client.handler;

import java.util.concurrent.TimeUnit;

import cn.songm.im.codec.Protocol;
import cn.songm.im.codec.Protocol.Operation;
import cn.songm.songmq.core.president.AbstractMessageHandler;
import cn.songm.songmq.core.president.MQProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.ScheduledFuture;

public class HeartbeatRequestHandler extends AbstractMessageHandler {

    private volatile ScheduledFuture<?> heartBeat;

    private class HeartBeatTask implements Runnable {
        private final ChannelHandlerContext ctx;

        public HeartBeatTask(ChannelHandlerContext ctx) {
            this.ctx = ctx;
        }

        @Override
        public void run() {
            ctx.writeAndFlush(buildMessage());
        }
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, MQProtocol msg)
            throws Exception {
        Protocol p = (Protocol) msg;
        if (Operation.LOGIN_RESP.equals(p.getOperation())) {
            // 开启心跳
            log.debug("heartbeat start");
            heartBeat = ctx.executor().scheduleAtFixedRate(
                    new HeartBeatTask(ctx), 0, 5000, TimeUnit.MILLISECONDS);
            return;
        }
        if (Operation.PONG.equals(p.getOperation())) {
            log.debug("response: {}", p.getOperation());
            return;
        }
        ctx.fireChannelRead(msg);
    }

    private Protocol buildMessage() {
        Protocol p = new Protocol();
        p.setOperation(Operation.PING);
        log.debug("request: {}", p.getOperation());
        return p;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        if (heartBeat != null) {
            heartBeat.cancel(true);
            heartBeat = null;
        }
        super.exceptionCaught(ctx, cause);
    }
}
