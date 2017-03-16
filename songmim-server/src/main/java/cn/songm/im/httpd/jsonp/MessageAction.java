package cn.songm.im.httpd.jsonp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.songm.common.utils.JsonUtils;
import cn.songm.im.IMException;
import cn.songm.im.httpd.HttpAction;
import cn.songm.im.model.Conversation;
import cn.songm.im.model.Result;
import cn.songm.im.model.SessionCh;
import cn.songm.im.model.message.Message;
import cn.songm.im.mqueue.ClientUser;
import cn.songm.im.service.ClientService;
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
        String body = getParamValue(decoder, "body");

        Result<Object> res = new Result<Object>();
        SessionCh ses = sessionService.getSession(session);

        Message msg = new Message();
        msg.setConv(conv);
        msg.setType(type);
        msg.setChId(chId);
        msg.setFrom(from);
        msg.setTo(to);
        msg.setJbody(body);

        byte[] bytes = JsonUtils.toJsonBytes(msg);
        ClientUser cUser = clientService.getClient(ses.getUid());
        cUser.publish(Conversation.Type.instance(msg.getConv()),
                msg.getFrom(), bytes);
        cUser.publish(Conversation.Type.instance(msg.getConv()),
                msg.getTo(), bytes);

        return HttpAction.callback(callback, res);
    }

}
