package songm.im.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class PollingController {

    @RequestMapping(value = "/polling", method = RequestMethod.GET)
    public String getToken(String token, String session, String callback) {
        
        return "data";
    }
}
