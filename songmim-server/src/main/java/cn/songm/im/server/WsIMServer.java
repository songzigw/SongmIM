package cn.songm.im.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.songm.im.IMConfig;
import cn.songm.im.handler.MessageDispatcher;
import cn.songm.im.handler.WsIMChInitializer;
import cn.songm.songmq.core.president.AbstractMQServer;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * WebSocket Broker 服务
 * 
 * @author zhangsong
 *
 */
@Component
public class WsIMServer extends AbstractMQServer {

    private static final Logger LOG = LoggerFactory
            .getLogger(WsIMServer.class);

    private WsIMChInitializer chInitializer;

    @Autowired
    public WsIMServer(MessageDispatcher dispatcher) {
        super(IMConfig.getInstance().getServerIp(),
                IMConfig.getInstance().getServerWsPort());
        this.init(dispatcher);
    }

    private void init(MessageDispatcher dispatcher) {
        chInitializer = new WsIMChInitializer(dispatcher);
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.SO_KEEPALIVE, false)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_SNDBUF, 65535)
                .option(ChannelOption.SO_RCVBUF, 65535)
                .handler(new LoggingHandler(LogLevel.INFO))
                .localAddress(serverIpAddr).childHandler(chInitializer);
    }

    @Override
    public void start() {
        new Thread(this, "SongmIMServerWs").start();
    }

    @Override
    public void shutdown() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        chInitializer.shutdownGracefully();
    }

    @Override
    public void run() {
        LOG.info("SongmIM server Ws start: {}:{}", serverIp, serverPort);
        ChannelFuture sync = null;
        try {
            sync = bootstrap.bind().sync();
            sync.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            this.shutdown();
            LOG.info("SongmIM server Ws shutdown: {}:{}", serverIp, serverPort);
        }
    }
}
