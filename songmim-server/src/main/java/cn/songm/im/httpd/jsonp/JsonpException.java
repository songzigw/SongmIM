package cn.songm.im.httpd.jsonp;

import cn.songm.im.IMException;

public class JsonpException extends IMException {

    private static final long serialVersionUID = 4216978718845306202L;

    private String callback;

    public JsonpException(ErrorCode errorCode, String description,
            String callback) {
        super(errorCode, description);
        this.callback = callback;
    }

    public String getCallback() {
        return callback;
    }
}
