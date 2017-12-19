package cn.songm.im.server.jsonp.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.songm.im.codec.IMException;
import cn.songm.im.codec.Result;
import cn.songm.im.codec.Session;
import cn.songm.im.codec.model.Conversation.Ctype;
import cn.songm.im.codec.model.Message;
import cn.songm.im.codec.model.Message.Mtype;
import cn.songm.im.server.jsonp.JsonpAction;
import cn.songm.im.service.ClientService;
import cn.songm.im.service.mqueue.ClientUser;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

@Component
public class MessageAction extends JsonpAction {

    @Autowired
    private ClientService clientService;

    @Override
    public String uri() {
        return Uri.POLLING_MESSAGE.getValue();
    }

    @Override
    public byte[] active(Channel ch, HttpRequest req) throws IMException {
        checkSession(req);

        QueryStringDecoder decoder = new QueryStringDecoder(req.uri());
        String session = getParamValue(decoder, "session");
        String chId = getParamValue(decoder, "chId");
        String callback = getParamValue(decoder, "callback");
        String conv = getParamValue(decoder, "conv");
        String type = getParamValue(decoder, "type");
        String from = getParamValue(decoder, "from");
        String to = getParamValue(decoder, "to");
        // String body = getParamValue(decoder, "body");

        Result<Object> res = new Result<Object>();
        Session ses = sessionService.getSession(session);

        Message msg = new Message();
        msg.setConv(Ctype.instance(conv));
        msg.setType(Mtype.instance(type));
        msg.setChId(chId);
        msg.setFrom(from);
        msg.setTo(to);
        // msg.setJbody(body);

        ClientUser cUser = clientService.getClient(ses.getUid());
        cUser.publish(msg.getFrom(), msg);
        cUser.publish(msg.getTo(), msg);

        return JsonpAction.callback(callback, res);
    }

}
