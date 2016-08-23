package songm.im.server;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.List;

import org.springframework.stereotype.Component;

import songm.im.entity.Protocol;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Component
@ChannelHandler.Sharable
public class WsocketProtocolCodec extends
        MessageToMessageCodec<TextWebSocketFrame, Protocol> {

    private final ObjectMapper jsonMapper = new ObjectMapper();

    @Override
    protected void encode(ChannelHandlerContext ctx,
            Protocol pro, List<Object> list) throws Exception {
        ObjectNode root = jsonMapper.createObjectNode();
        JsonNode body = jsonMapper.readTree(pro.getBody());
        root.put("ver", pro.getVersion());
        root.put("op", pro.getOperation());
        root.put("seq", pro.getSequence());
        root.set("body", body);

        list.add(new TextWebSocketFrame(root.toString()));
    }

    @Override
    protected void decode(ChannelHandlerContext ctx,
            TextWebSocketFrame textWebSocketFrame, List<Object> list)
            throws Exception {
        String text = textWebSocketFrame.text();
        JsonNode root = jsonMapper.readTree(text);
        Protocol pro = new Protocol();
        if (root.has("ver")) {
            pro.setVersion(root.get("ver").shortValue());
        }
        if (root.has("op")) {
            pro.setOperation(root.get("op").asInt());
        }
        if (root.has("seq")) {
            pro.setSequence(root.get("seq").asLong());
        }
        if (root.has("body")) {
            pro.setBody(root.get("body").toString().getBytes());
        }
        list.add(pro);
    }
}
