package songm.im.web;

import io.netty.channel.Channel;

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
    public String longPolling(String token, String session, String callback) {
        ModelAndView mv = new ModelAndView();
        try {
            // 连接成功
            Session newSes = authService.online(token, session, ch);
        } catch (IMException e) {
            // 连接失敗
            session.setErrorCode(e.getErrorCode().name());
        }
        return "/data";
    }
    
    @RequestMapping(value = "/message", method = RequestMethod.GET)
    public String sendMessage(String session, String chId, String from, String to, int type, String text, String callback) {
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
        
        Message msg = new Message();
        msg.setFrom(from);
        msg.setTo(to);
        msg.setBody(text);
        
        Channel ch = ses.getChannel(chId);
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
