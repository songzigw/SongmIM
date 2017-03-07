package cn.songm.im.httpd;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.util.concurrent.EventExecutorGroup;

@Component
public class HttpChInitializer extends ChannelInitializer<SocketChannel> {

    @Autowired
    private HttpMsgHandler httpMsgHandler;
    // DefaultEventExecutorGroup
    private EventExecutorGroup eExeGroup;

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline p = ch.pipeline();
        // if (sslCtx != null) {
        // p.addLast(sslCtx.newHandler(ch.alloc()));
        // }
        p.addLast(eExeGroup);
        p.addLast(new HttpServerCodec());
        p.addLast(httpMsgHandler);
    }

    public void shutdownGracefully() {
        if (eExeGroup != null) {
            eExeGroup.shutdownGracefully();
        }
    }
}
