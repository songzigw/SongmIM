package cn.songm.im.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import cn.songm.im.codec.Codec;
import cn.songm.im.server.handler.ActionDispatcherHandler;
import cn.songm.im.server.handler.DisconnectHandler;
import cn.songm.im.server.handler.HeartbeatResponseHandler;
import cn.songm.im.server.handler.LoginAuthResponseHandler;
import cn.songm.songmq.core.president.AbstractMQServer;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;

@Component
public class IMServer extends AbstractMQServer {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    public IMServer(@Value("${songmim.server.ip}") String ip,
            @Value("${songmim.server.tcp.port}") int port) {
        super(ip, port);
        this.init();
    }

    @Autowired
    private LoginAuthResponseHandler loginAuthResponseHandler;
    @Autowired
    private DisconnectHandler disconnectHandler;
    @Autowired
    private HeartbeatResponseHandler heartbeatResponseHandler;
    @Autowired
    private ActionDispatcherHandler actionDispatcherHandler;
    
    
    private void init() {
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class);
        bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
        // 码流日志打印
        bootstrap.handler(new LoggingHandler(LogLevel.INFO));
        // 处理器链加入更多处理器
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {

            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast("codec", new Codec());
                ch.pipeline().addLast("readTimeoutHandler",
                        new ReadTimeoutHandler(50));
                // 登入授权验证
                ch.pipeline().addLast("loginAuthResponseHandler", loginAuthResponseHandler);
                ch.pipeline().addLast("disconnectHandler", disconnectHandler);
                // 心跳检测机制
                ch.pipeline().addLast("heartbeatResponseHandler", heartbeatResponseHandler);
                ch.pipeline().addLast("actionDispatcherHandler", actionDispatcherHandler);
                // eExecutorGroup
            }

        });
    }

    @Override
    public void startup() {
        new Thread(this, "songmIMServerTCP").start();
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
