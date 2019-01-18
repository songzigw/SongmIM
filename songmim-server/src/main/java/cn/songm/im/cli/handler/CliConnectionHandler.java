package cn.songm.im.cli.handler;

import java.util.concurrent.TimeUnit;

import cn.songm.im.server.command.codec.Protocol;
import cn.songm.im.server.command.codec.Protocol.Operation;
import cn.songm.songmq.core.CallbackInvoker;
import cn.songm.songmq.core.CallbackManager;
import cn.songm.songmq.core.president.AbstractMessageHandler;
import cn.songm.songmq.core.president.MQProtocol;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

public class CliConnectionHandler extends AbstractMessageHandler {

    private boolean isConnected;
    
    public boolean isConnected() {
	return isConnected;
    }
    
    @Override
    protected void messageReceived(ChannelHandlerContext ctx, MQProtocol msg) throws Exception {
	Protocol p = (Protocol) msg;
	if (!Operation.CONNECT_ACK.equals(p.getOperation())) {
	    ctx.fireChannelRead(msg);
	    return;
	}
	String result = new String(p.getBody());
	CallbackManager.getInstance().trigger(p.getSequence(), result);
	if ("failure".equals(result)) {
	    this.isConnected = false;
	    ctx.close().syncUninterruptibly();
	    return;
	}
	this.isConnected = true;
	ctx.fireChannelRead(msg);
    }
    
    public String connect(Channel ch) {
	Protocol p = new Protocol();
	p.setOperation(Operation.CONNECT_REQ);
	CallbackInvoker invoker = new CallbackInvoker(p.getSequence());
	CallbackManager.getInstance().put(invoker);
	ch.writeAndFlush(p);
	return (String) invoker.getResult(60 * 1000, TimeUnit.MILLISECONDS);
    }
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
	// none
    }
    
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
	this.isConnected = false;
    }
}