/*
 * Copyright (c) 2016, zhangsong <songm.cn>.
 *
 */
package songm.im.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Tcp管道初始化
 *
 * @author zhangsong
 * @since 0.1, 2016-8-9
 * @version 0.1
 * 
 */
@Component
public class TcpServerInitializer extends ChannelInitializer<SocketChannel> {

    @Autowired
    private TcpProtocolCodec protocolCodec;
    @Autowired
    private IMServerHandler serverHandler;

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(protocolCodec);
        ch.pipeline().addLast(serverHandler);
    }

}
