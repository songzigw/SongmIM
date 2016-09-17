/*
 * Copyright [2016] [zhangsong <songm.cn>].
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package songm.im.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import songm.im.IMException;
import songm.im.IMException.ErrorCode;
import songm.im.IMServer;

/**
 * Tcp连接服务
 *
 * @author zhangsong
 * @since 0.1, 2016-8-9
 * @version 0.1
 * 
 */
@Component("tcpIMServer")
public class TcpIMServer implements IMServer {

    private static final Logger LOG = LoggerFactory.getLogger(IMServer.class);

    @Value("${server.tcp.port}")
    private int port;
    @Autowired
    private TcpServerInitializer serverInitializer;

    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workGroup;
    private ChannelFuture channelFuture;

    private boolean running;
    
    public TcpIMServer() {
        bossGroup = new NioEventLoopGroup();
        workGroup = new NioEventLoopGroup();
    }

    @Override
    public void start() throws IMException {
        try {
            ServerBootstrap b = new ServerBootstrap()
                    .group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(serverInitializer);
            channelFuture = b.bind(port).sync();
            running = true;
        } catch (InterruptedException e) {
            String message = "Start TcpIMServer failure";
            LOG.error(message, e);
            throw new IMException(ErrorCode.START_ERROR , message, e);
        } finally {
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    shutdown();
                }
            });
        }
        LOG.info("Start TcpIMServer Port:{}", port);
    }

    @Override
    public void restart() throws IMException {
        shutdown();
        start();
    }

    @Override
    public void shutdown() {
        if (channelFuture != null) {
            channelFuture.channel().close().syncUninterruptibly();
        }
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        if (workGroup != null) {
            workGroup.shutdownGracefully();
        }
        running = false;
    }

    @Override
    public boolean isRunning() {
        return running;
    }
}
