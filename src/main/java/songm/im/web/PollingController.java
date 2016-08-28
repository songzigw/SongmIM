package songm.im.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 长轮询相关服务
 * 
 * @author zhangsong
 *
 */
@Controller
@RequestMapping("/polling")
public class PollingController {

    /**
     * 长轮询
     * 
     * @param token
     * @param session
     * @param callback
     * @return
     */
    @RequestMapping(value = "/long", method = RequestMethod.GET)
    public String getToken(String token, String session, String callback) {

        return "/data";
    }
}
