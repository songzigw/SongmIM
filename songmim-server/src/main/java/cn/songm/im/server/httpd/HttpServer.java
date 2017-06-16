package cn.songm.im.server.httpd;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import cn.songm.im.business.IMConfig;
import cn.songm.songmq.core.president.MQServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

@Component
public class HttpServer implements MQServer{

    private static final Logger LOG = LoggerFactory
            .getLogger(HttpServer.class);

    private String serverIp;
    private Integer serverPort;
    
    private SocketAddress serverIpAddr;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ServerBootstrap bootstrap;
    
    @Resource(name = "httpChInitializer")
    private HttpChInitializer chInitializer;

    public HttpServer() {
        this.serverIp = IMConfig.getInstance().getServerIp();
        this.serverPort = IMConfig.getInstance().getHttpdPort();
        
        serverIpAddr = new InetSocketAddress(serverIp, serverPort);
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        
        bootstrap = new ServerBootstrap();
    }
    
    @Override
    public void start() {
        new Thread(this, "SongmIMHttpd").start();
    }

    @Override
    public void shutdown() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        chInitializer.shutdownGracefully();
    }

    @Override
    public void run() {
        bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
        bootstrap.group(bossGroup, workerGroup)
         .channel(NioServerSocketChannel.class)
         .handler(new LoggingHandler(LogLevel.INFO))
         .localAddress(serverIpAddr)
         .childHandler(chInitializer);

        LOG.info("SongmIM httpd start: {}:{}", serverIp, serverPort);
        try {
            ChannelFuture future = bootstrap.bind().sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            this.shutdown();
            LOG.info("SongmIM httpd shutdown: {}:{}", serverIp, serverPort);
        }
    }

    public String getServerIp() {
        return serverIp;
    }

    public Integer getServerPort() {
        return serverPort;
    }

    public SocketAddress getServerIpAddr() {
        return serverIpAddr;
    }
}
