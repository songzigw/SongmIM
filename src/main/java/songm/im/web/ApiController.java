package songm.im.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import songm.im.entity.Result;
import songm.im.entity.Token;
import songm.im.service.AuthService;
import songm.im.utils.JsonUtils;

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
    @RequestMapping(value = "/token", method = RequestMethod.GET)
    public String getToken(String uid, String nick, String avatar) {
        Token token = authService.createToken(uid, nick, avatar);
        Result<Token> res = new Result<Token>();
        res.setData(token);
        
        ModelAndView mv = new ModelAndView();
        mv.addObject("data", JsonUtils.toJson(res, res.getClass()));
        return "/data";
    }
}
