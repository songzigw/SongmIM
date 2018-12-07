package cn.songm.im.server.httpapi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import cn.songm.songmq.core.president.AbstractMQServer;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

@Component
public class ApiServer extends AbstractMQServer {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private ApiDispatcherHandler apiDispatcherHandler;
    
    @Autowired
    public ApiServer(@Value("${songmim.server.ip}") String ip,
            @Value("${songmim.server.api.port}") int port) {
        super(ip, port);
        this.init();
    }

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
                //ch.pipeline().addLast("httpServerCodec", new HttpServerCodec());
                ch.pipeline().addLast("http-decoder", new HttpRequestDecoder());
                ch.pipeline().addLast("http-aggregator", new HttpObjectAggregator(65536));
                ch.pipeline().addLast("http-encoder", new HttpResponseEncoder());
                ch.pipeline().addLast("http-chunked", new ChunkedWriteHandler());
                ch.pipeline().addLast("apiDispatcherHandler", apiDispatcherHandler);
                // eExecutorGroup
            }

        });
    }

    @Override
    public void startup() {
        new Thread(this, "songmIMServerAPI").start();
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
