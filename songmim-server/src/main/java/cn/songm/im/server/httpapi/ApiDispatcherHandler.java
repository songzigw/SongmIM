package cn.songm.im.server.httpapi;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaderNames.LOCATION;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.songm.im.codec.IMException;
import cn.songm.im.codec.Result;
import cn.songm.im.server.httpapi.action.ApiActionContainer;
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
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.CharsetUtil;

@Component
@ChannelHandler.Sharable
public class ApiDispatcherHandler extends ChannelHandlerAdapter {

    private final Logger log = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private ApiActionContainer actionManager;

    /**
     * 发送错误信息
     * @param ctx
     * @param status
     */
    private void sendError(ChannelHandlerContext ctx, HttpResponseStatus status) {
	ByteBuf buf = Unpooled.copiedBuffer(status.toString(), CharsetUtil.UTF_8);
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, status, buf);
        response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
    
    /**
     * URI 重定向
     * @param ctx
     * @param newUri
     */
    private static void sendRedirect(ChannelHandlerContext ctx, String newUri) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.FOUND);
        response.headers().set(LOCATION, newUri);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
    
    /**
     * 发送正常的响应信息
     * @param ctx
     * @param bytes
     */
    private static <T> void sendAck(ChannelHandlerContext ctx, byte[] bytes) {
	ByteBuf buf = Unpooled.wrappedBuffer(bytes);
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK);
        response.content().writeBytes(buf);
        buf.release();
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/json; charset=UTF-8");
        response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
    
    //@Override
    //public void channelReadComplete(ChannelHandlerContext ctx) {
    //    ctx.flush();
    //}

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("server error", cause);
        if (ctx.channel().isActive()) {
            sendError(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws UnsupportedEncodingException {
        FullHttpRequest request = (FullHttpRequest) msg;
        // 判断解码是否成功
        if (!request.decoderResult().isSuccess()) {
            sendError(ctx, HttpResponseStatus.BAD_REQUEST);
            return;
        }
        
        ApiAction action = actionManager.find(request.uri().split("\\?")[0]);
        if (action == null) {
            sendError(ctx, HttpResponseStatus.NOT_FOUND);
            return;
        }
        byte[] bytes = null;
        try {
            bytes = action.active(ctx.channel(), request);
        } catch (IMException e) {
            Result<Object> result = new Result<Object>();
            result.setErrCode(e.getErrorCode().getCode());
            result.setErrDesc(e.getErrorDesc());
            bytes = JsonUtils.getInstance().toJsonBytes(result);
        }
        sendAck(ctx, bytes);
    }

}
