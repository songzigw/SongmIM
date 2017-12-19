package cn.songm.im.server.api;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaderNames.LOCATION;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.FORBIDDEN;
import static io.netty.handler.codec.http.HttpResponseStatus.FOUND;
import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.songm.im.codec.IMException;
import cn.songm.im.codec.Result;
import cn.songm.im.server.api.action.ApiActionContainer;
import cn.songm.songmq.core.util.JsonUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.CharsetUtil;

@Component
@ChannelHandler.Sharable
public class ApiDispatcherHandler extends ChannelHandlerAdapter {

    private final Logger log = LoggerFactory.getLogger(getClass());
    
    private static final Pattern INSECURE_URI = Pattern.compile(".*[<>&\"].*");
    
    //private static final Pattern ALLOWED_FILE_NAME = Pattern.compile("[A-Za-z0-9][-_A-Za-z0-9\\.]*");
    
    @Autowired
    private ApiActionContainer actionManager;

    private void sendError(ChannelHandlerContext ctx, HttpResponseStatus status) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1,
                status, Unpooled.copiedBuffer("Failure: " + status.toString()
                        + "\r\n", CharsetUtil.UTF_8));
        response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
    
    private String sanitizeUri(String uri) throws UnsupportedEncodingException {
        try {
            uri = URLDecoder.decode(uri, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw e;
        }
        
        if (!INSECURE_URI.matcher(uri).matches()) {
            return null;
        }
        return uri;
    }
    
    private static void sendRedirect(ChannelHandlerContext ctx, String newUri) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, FOUND);
        response.headers().set(LOCATION, newUri);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
    
    private static <T> void sendAck(ChannelHandlerContext ctx, byte[] bytes) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK);
        response.headers().set(CONTENT_TYPE, "text/json;charst=UTF-8");
        ByteBuf buf = Unpooled.wrappedBuffer(bytes);
        response.content().writeBytes(buf);
        buf.release();
        //response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH,
        //        response.content().readableBytes());
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);

    }
    
    //@Override
    //public void channelReadComplete(ChannelHandlerContext ctx) {
    //    ctx.flush();
    //}

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error(cause.getMessage(), cause);
        if (ctx.channel().isActive()) {
            sendError(ctx, INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws UnsupportedEncodingException {
        FullHttpRequest request = (FullHttpRequest) msg;
        if (!request.decoderResult().isSuccess()) {
            sendError(ctx, BAD_REQUEST);
            return;
        }
        
        final String uri = sanitizeUri(request.uri());
        if (uri == null) {
            sendError(ctx, FORBIDDEN);
            return;
        }
        
        ApiAction action = actionManager.find(uri.split("\\?")[0]);
        if (action == null) {
            sendError(ctx, HttpResponseStatus.NOT_FOUND);
            return;
        }
        byte[] bytes = null;
        try {
            bytes = action.active(ctx.channel(), request);
        } catch (IMException e) {
            Result<Object> result = new Result<Object>();
            result.setErrorCode(e.getErrorCode().getCode());
            result.setMessage(e.getErrorDesc());
            bytes = JsonUtils.getInstance().toJsonBytes(result);
        }
        sendAck(ctx, bytes);
    }

}
