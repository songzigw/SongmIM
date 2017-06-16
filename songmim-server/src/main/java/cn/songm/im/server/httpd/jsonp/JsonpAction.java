package cn.songm.im.server.httpd.jsonp;

import org.springframework.beans.factory.annotation.Autowired;

import cn.songm.im.business.IMException;
import cn.songm.im.business.IMException.ErrorCode;
import cn.songm.im.business.SessionCh;
import cn.songm.im.business.service.SessionService;
import cn.songm.im.server.httpd.HttpAction;
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
