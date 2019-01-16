package cn.songm.im.server.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import cn.songm.im.server.command.codec.Codec;
import cn.songm.im.server.handler.HeartbeatResponseHandler;
import cn.songm.songmq.core.president.AbstractMQServer;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;

/**
 * 命令行服务器
 * @author zhangsong
 *
 */
@Component
public class CmdServer extends AbstractMQServer {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    public CmdServer(
	    @Value("${songmim.server.ip}") String ip,
            @Value("${songmim.server.cli.port}") int port) {
        super(ip, port);
        this.init();
    }

    private void init() {
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class);
        bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
        // 码流日志打印
        bootstrap.handler(new LoggingHandler(LogLevel.INFO));
        // 管道链中加入处理器
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {

            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
        	// 编码解码器
        	ch.pipeline().addLast("codec", new Codec());
                // 超时处理器
        	ch.pipeline().addLast("readTimeoutHandler",
                        new ReadTimeoutHandler(50));
                // 连接授权验证
                ch.pipeline().addLast("cmdConnectionHandler",
                        new CmdConnectionHandler());
                // 链接断开处理
                ch.pipeline().addLast("cmdDisconnectHandler",
                        new CmdDisconnectHandler());
                // 心跳检测机制
                ch.pipeline().addLast("heartbeatResponseHandler",
                        new HeartbeatResponseHandler());
                // 业务分发处理
                ch.pipeline().addLast("cmsDispatcherHandler", new CmsDispatcherHandler());
                // eExecutorGroup
            }

        });
    }

    @Override
    public void startup() {
        new Thread(this, "songmIMServerCMD").start();
    }

    @Override
    public void shutdown() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        if (eExecutorGroup != null)
        eExecutorGroup.shutdownGracefully();
        channelFuture.channel().close().syncUninterruptibly();
    }

    @Override
    public void run() {
        try {
            // 绑定端口，同步等待成功
            channelFuture = bootstrap.bind(serverIpAddr).sync();
            log.info("{} {}:{} started.", Thread.currentThread().getName(),
                    serverIpAddr.getHostString(), serverIpAddr.getPort());
            // 等待服务端监听端口关闭
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            // 优雅退出，释放线程池资源
            this.shutdown();
            log.info("{} {}:{} stopped.", Thread.currentThread().getName(),
                    serverIpAddr.getHostString(), serverIpAddr.getPort());
        }
    }
}
