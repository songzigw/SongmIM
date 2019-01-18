package cn.songm.im.cli.handler;

import cn.songm.im.server.command.codec.Protocol;
import cn.songm.im.server.command.codec.Protocol.Operation;
import io.netty.channel.Channel;

public class CliDisconnectHandler {

    public static void buildMessage(Channel ch) {
	Protocol p = new Protocol();
	p.setOperation(Operation.DISCONNECT);
	ch.writeAndFlush(p);
    }
}
