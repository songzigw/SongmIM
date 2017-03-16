package cn.songm.im.httpd.jsonp;

import org.springframework.beans.factory.annotation.Autowired;

import cn.songm.im.IMException;
import cn.songm.im.IMException.ErrorCode;
import cn.songm.im.httpd.HttpAction;
import cn.songm.im.model.SessionCh;
import cn.songm.im.service.SessionService;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;

public abstract class JsonpAction extends HttpAction {

    @Autowired
    protected SessionService sessionService;

    public void checkSession(HttpRequest req) throws IMException {
        HttpHeaders headers = req.headers();
        String sesId = headers.getAndConvert("session");
        SessionCh ses = sessionService.getSession(sesId);
        if (ses == null) {
            throw new IMException(ErrorCode.SESSION_DISABLED,
                    "Session disabled.");
        }
    }
}
