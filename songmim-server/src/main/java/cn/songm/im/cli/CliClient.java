package cn.songm.im.cli;

import cn.songm.im.cli.handler.CliConnectionHandler;
import cn.songm.im.cli.handler.CliDisconnectHandler;
import cn.songm.im.cli.handler.CliHeartbeatHandler;
import cn.songm.im.server.command.codec.Codec;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;

/**
 * 命令行客户端
 * 
 * @author zhangsong
 *
 */
public class CliClient {

    private String host;
    private int port;
    
    // 链接授权请求
    private CliConnectionHandler cliConnectionHandler;
    // 心跳请求处理
    private CliHeartbeatHandler cliHeartbeatHandler;
    
    public CliClient(String host, int port) {
	cliConnectionHandler = new CliConnectionHandler();
	cliHeartbeatHandler = new CliHeartbeatHandler();
	this.host = host;
	this.port = port;
	this.init();
    }
    
    private Bootstrap b;
    private EventLoopGroup g;
    private ChannelFuture f;
    
    private void init() {
	g = new NioEventLoopGroup();
	b = new Bootstrap();
	b.group(g).channel(NioSocketChannel.class);
	b.remoteAddress(host, port);
	b.handler(new ChannelInitializer<SocketChannel>() {
	    @Override
	    protected void initChannel(SocketChannel ch) throws Exception {
		ch.pipeline().addLast("codec", new Codec());
		ch.pipeline().addLast("readTimeoutHandler", new ReadTimeoutHandler(50));
		ch.pipeline().addLast("cliConnectionHandler", cliConnectionHandler);
		ch.pipeline().addLast("cliHeartbeatHandler", cliHeartbeatHandler);
	    }
	});
    }
    
    public void connect() throws InterruptedException {
	f = b.connect().sync();
	String r = cliConnectionHandler.connect(f.channel());
	if (r.equals("failure")) {
	    throw new InterruptedException(r);
	}
	
	new Thread(new Runnable() {

	    @Override
	    public void run() {
		try {
		    closeFuture();
		} catch (InterruptedException e) {
		    
		}
	    }
	    
	    private void closeFuture() throws InterruptedException {
		try {
		    f.channel().closeFuture().sync();
		} finally {
		    g.shutdownGracefully().sync();
		}
	    }
	    
	}, "cliClient").start();
    }
    
    public void disconnect() {
	CliDisconnectHandler.buildMessage(f.channel());
    }
}
