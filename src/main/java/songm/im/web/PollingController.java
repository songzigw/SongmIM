package songm.im.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import songm.im.IMException;
import songm.im.IMException.ErrorCode;
import songm.im.entity.Entity;
import songm.im.entity.Message;
import songm.im.entity.Session;
import songm.im.entity.SessionCh;
import songm.im.message.MessageContent;
import songm.im.message.TextMessage;
import songm.im.server.ChannelLongPolling;
import songm.im.service.AuthService;
import songm.im.service.ClientService;
import songm.im.service.SessionService;
import songm.im.utils.JsonUtils;

/**
 * 长轮询相关服务
 * 
 * @author zhangsong
 *
 */
@Controller
@RequestMapping("/polling")
public class PollingController {
    
    private static final long TIME_OUT = 30 * 1000;

    @Autowired
    private AuthService authService;
    @Autowired
    private SessionService sessionService;
    @Autowired
    private ClientService clientService;

    private SessionCh getSession(String session) {
        return sessionService.getSession(session);
    }

    /**
     * 长轮询
     * 
     * @param token
     * @param session
     * @param callback
     * @return
     */
    @RequestMapping(value = "/long", method = RequestMethod.GET)
    public String longPolling(String token, String session, String chId, String callback) {
        ModelAndView mv = new ModelAndView();
        
        SessionCh ses = null;
        ChannelLongPolling clp = new ChannelLongPolling(chId);
        try {
            // 连接成功
            ses = authService.online(token, session, clp);
        } catch (IMException e) {
            // 连接失败
            Session s = new Session();
            s.setSucceed(false);
            s.setErrorCode(e.getErrorCode().name());
            mv.addObject("data", callback + "("
                    + JsonUtils.toJson(s, Session.class) + ")");
            return "/data";
        }
        
        // 第一次连接成功
        if (ses.isFirstConn(chId)) {
            ses.setAttribute("ch_id", clp.getChId());
            mv.addObject("data", callback + "("
                    + JsonUtils.toJson(ses, Session.class) + ")");
            return "/data";
        }
        
        // 获取消息
        long start = System.currentTimeMillis();
        ChannelLongPolling ch = ses.getChannel(chId);
        byte[] message = null;
        do {
            message = ch.getMessage();
            if (message != null) {
                break;
            }
            if (System.currentTimeMillis() - start > TIME_OUT) {
                message = new byte[] {};
                break;
            }
        } while (true);
        
        mv.addObject("data", callback + "("
                + JsonUtils.toJson(message, Message.class) + ")");
        return "/data";
    }
    
    @RequestMapping(value = "/message", method = RequestMethod.GET)
    public String sendMessage(String session, String chId, String from, String to, String text, String callback) {
        ModelAndView mv = new ModelAndView();
        Entity ent = new Entity();

        // Session是否正确
        SessionCh ses = getSession(session);
        if (ses == null) {
            ent.setSucceed(false);
            ent.setErrorCode(ErrorCode.SESSION_DISABLED.name());
            mv.addObject("data", callback + "("
                    + JsonUtils.toJson(ent, Entity.class) + ")");
            return "/data";
        }
        
        TextMessage tm = new TextMessage();
        tm.setText(text);
        Message msg = new Message();
        msg.setType(MessageContent.TEXT);
        msg.setFrom(from);
        msg.setTo(to);
        msg.setJsonBody(tm.getJsonString());
        
        ChannelLongPolling ch = ses.getChannel(chId);
        byte[] bytes = JsonUtils.toJson(msg, Message.class).getBytes();
        clientService.getClient(msg.getFrom()).trigger(bytes, ch);
        try {
            clientService.publish(msg.getFrom(), msg.getTo(), bytes);
        } catch (IMException e) {
            ent.setSucceed(false);
            ent.setErrorCode(e.getErrorCode().name());
        }
        
        mv.addObject("data", callback + "("
                + JsonUtils.toJson(ent, Entity.class) + ")");
        return "/data";
    }
}
