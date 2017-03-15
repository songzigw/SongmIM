package cn.songm.im.httpd.polling;

import cn.songm.im.IMException;

public class PollingException extends IMException {

    private static final long serialVersionUID = 4216978718845306202L;

    private String callback;

    public PollingException(ErrorCode errorCode, String description,
            String callback) {
        super(errorCode, description);
        this.callback = callback;
    }

    public String getCallback() {
        return callback;
    }
}
