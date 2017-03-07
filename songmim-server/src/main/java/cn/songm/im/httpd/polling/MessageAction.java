package cn.songm.im.httpd.polling;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.songm.common.utils.JsonUtils;
import cn.songm.im.IMException;
import cn.songm.im.IMException.ErrorCode;
import cn.songm.im.model.ChLongPolling;
import cn.songm.im.model.Conversation;
import cn.songm.im.model.Result;
import cn.songm.im.model.SessionCh;
import cn.songm.im.model.message.Message;
import cn.songm.im.model.message.TextMessage;
import cn.songm.im.mqueue.ClientUser;
import cn.songm.im.service.ClientService;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

@Component
public class MessageAction extends PollingAction {

    @Autowired
    private ClientService clientService;

    @Override
    public String uri() {
        return Uri.POLLING_MESSAGE.getValue();
    }

    @Override
    public byte[] active(Channel ch, HttpRequest req) throws IMException {
        QueryStringDecoder decoder = new QueryStringDecoder(req.uri());
        String chId = getParamValue(decoder, "chId");
        String session = getParamValue(decoder, "session");
        String callback = getParamValue(decoder, "callback");
        String conv = getParamValue(decoder, "conv");
        String type = getParamValue(decoder, "type");
        String from = getParamValue(decoder, "from");
        String to = getParamValue(decoder, "to");
        String body = getParamValue(decoder, "body");

        Result<Object> res = new Result<Object>();
        // Session是否正确
        SessionCh ses = sessionService.getSession(session);
        if (ses == null) {
            res.setErrorCode(ErrorCode.SESSION_DISABLED.name());
            res.setErrorDesc("Session失效");
            return (callback + "(" + JsonUtils.toJson(res, res.getClass())
                    + ")").getBytes();
        }

        Message msg = new Message();
        msg.setConv(conv);
        msg.setType(type);
        msg.setFrom(from);
        msg.setTo(to);
        TextMessage tm = new TextMessage();
        tm.setText(body);
        msg.setBody(tm);

        ChLongPolling chLp = ses.getChannel(chId);
        byte[] bytes = JsonUtils.toJsonBytes(msg, Message.class);
        ClientUser cUser = clientService.getClient(msg.getFrom());
        cUser.trigger(bytes, chLp);
        try {
            cUser.publish(Conversation.Type.instance(msg.getConv()),
                    msg.getTo(), bytes);
        } catch (IMException e) {
            res.setErrorCode(e.getErrorCode().name());
            res.setErrorDesc(e.getDescription());
        }

        return (callback + "(" + JsonUtils.toJson(res, res.getClass()) + ")")
                .getBytes();
    }

}
