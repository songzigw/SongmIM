package cn.songm.im.handler;

import cn.songm.im.handler.codec.TcpProtocolCodec;
import cn.songm.songmq.core.president.AbstractChInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * Tcp 管道初始化器
 * 
 * @author zhangsong
 *
 */
public class TcpIMChInitializer extends AbstractChInitializer {

    public TcpIMChInitializer(MessageDispatcher msgHandler) {
        super(msgHandler);
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();
        p.addLast(new TcpProtocolCodec());
        super.initChannel(ch);
    }
}
