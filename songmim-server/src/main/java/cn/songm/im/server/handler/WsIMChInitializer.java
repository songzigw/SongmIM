package cn.songm.im.server.handler;

import cn.songm.im.server.handler.codec.WsProtocolCodec;
import cn.songm.songmq.core.president.AbstractChInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * WebSocket 管道初始化器
 * 
 * @author zhangsong
 *
 */
public class WsIMChInitializer extends AbstractChInitializer {

    public WsIMChInitializer(MessageDispatcher msgHandler) {
        super(msgHandler);
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        // 编解码 http 请求
        pipeline.addLast(new HttpServerCodec());
        // 写文件内容
        pipeline.addLast(new ChunkedWriteHandler());
        // 聚合解码 HttpRequest/HttpContent/LastHttpContent
        // 到 FullHttpRequest 保证接收的 Http 请求的完整性
        pipeline.addLast(new HttpObjectAggregator(64 * 1024));
        // 处理其他的 WebSocketFrame
        pipeline.addLast(new WebSocketServerProtocolHandler("/im"));
        // 处理 TextWebSocketFrame
        pipeline.addLast(new WsProtocolCodec());
        super.initChannel(ch);
    }
}
