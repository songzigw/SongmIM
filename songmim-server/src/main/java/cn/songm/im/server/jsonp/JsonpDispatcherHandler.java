package cn.songm.im.server.jsonp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.songm.im.codec.IMException;
import cn.songm.im.codec.Result;
import cn.songm.im.server.jsonp.action.JsonpActionContainer;
import cn.songm.songmq.core.util.JsonUtils;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderUtil;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.QueryStringDecoder;

@Component
@ChannelHandler.Sharable
public class JsonpDispatcherHandler extends ChannelHandlerAdapter {

    @Autowired
    private JsonpActionContainer actionManager;

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (!(msg instanceof HttpRequest)) {
            return;
        }

        HttpRequest req = (HttpRequest) msg;
        DefaultFullHttpResponse response = null;
        if (HttpHeaderUtil.is100ContinueExpected(req)) {
            ctx.write(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                    HttpResponseStatus.CONTINUE));
        }

        JsonpAction action = actionManager.find(req.uri().split("\\?")[0]);
        if (action != null) {
            byte[] bytes = null;
            try {
                bytes = action.active(ctx.channel(), req);
            } catch (IMException e) {
                Result<Object> result = new Result<Object>();
                result.setErrCode(e.getErrorCode().getCode());
                result.setErrDesc(e.getErrorDesc());
                String callback = null;
                if (req.uri().startsWith("/jsonp/")) {
                    String param = "callback";
                    QueryStringDecoder decoder = new QueryStringDecoder(req.uri());
                    if (decoder.parameters().get(param) != null
                            && !decoder.parameters().get(param).isEmpty()) {
                         callback = decoder.parameters().get(param).get(0);
                    }
                }
                if (callback != null) {
                    bytes = JsonpAction.callback(callback, result);
                } else {
                    bytes = JsonUtils.getInstance().toJsonBytes(result);
                }
            }
            response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                    HttpResponseStatus.OK, Unpooled.wrappedBuffer(bytes));
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/json");
            response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH,
                    response.content().readableBytes());
        } else {
            response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                    HttpResponseStatus.NOT_FOUND);
        }

        if (!HttpHeaderUtil.isKeepAlive(req)) {
            ctx.write(response).addListener(ChannelFutureListener.CLOSE);
        } else {
            // response.headers().set(HttpHeaderNames.CONNECTION,
            // HttpHeaderValues.KEEP_ALIVE);
            ctx.write(response).addListener(ChannelFutureListener.CLOSE);
        }
    }

}
