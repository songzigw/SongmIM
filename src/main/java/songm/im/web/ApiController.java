package songm.im.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import songm.im.service.AuthService;

/**
 * 第三方后台服务API
 * 
 * @author songzigw
 *
 */
@Controller
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private AuthService authService;

    /**
     * 获取Token
     * 
     * @param uid
     * @param nick
     * @param avatar
     * @return
     */
    @RequestMapping(value = "/token", method = RequestMethod.POST)
    public String getToken(String uid, String nick, String avatar) {

        return "/data";
    }
}
