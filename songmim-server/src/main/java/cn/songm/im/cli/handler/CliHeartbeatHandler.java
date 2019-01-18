package cn.songm.im.cli.handler;

import java.util.concurrent.TimeUnit;

import cn.songm.im.server.command.codec.Protocol;
import cn.songm.im.server.command.codec.Protocol.Operation;
import cn.songm.songmq.core.president.AbstractMessageHandler;
import cn.songm.songmq.core.president.MQProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.ScheduledFuture;

public class CliHeartbeatHandler extends AbstractMessageHandler {

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
    protected void messageReceived(ChannelHandlerContext ctx, MQProtocol msg) {
	Protocol p = (Protocol) msg;
	if (Operation.CONNECT_ACK.equals(p.getOperation())) {
	    heartBeat = ctx.executor().scheduleAtFixedRate(
		    new HeartBeatTask(ctx), 0, 5000, TimeUnit.MILLISECONDS);
	    return;
	}
	if (Operation.PONG.equals(p.getOperation())) {
	    return;
	}
	ctx.fireChannelRead(msg);
    }
    
    private Protocol buildMessage() {
	Protocol p = new Protocol();
	p.setOperation(Operation.PING);
	return p;
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
	if (heartBeat != null) {
	    heartBeat.cancel(true);
	    heartBeat = null;
	}
	super.exceptionCaught(ctx, cause);
    }
}
