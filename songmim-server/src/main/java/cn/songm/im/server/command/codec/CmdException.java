package cn.songm.im.server.command.codec;

/**
 * 异常类
 * 
 * @author zhangsong
 *
 */
public class CmdException extends Exception {

    private static final long serialVersionUID = -2104326166984622545L;

    private ErrorCode errorCode;

    private String errorDesc;

    public CmdException(ErrorCode errorCode, String errorDesc) {
        super(errorCode + ":" + errorDesc);
        this.errorCode = errorCode;
        this.errorDesc = errorDesc;
    }

    public CmdException(ErrorCode errorCode, String errorDesc,
            Throwable cause) {
        super(errorCode + ":" + errorDesc, cause);
        this.errorCode = errorCode;
        this.errorDesc = errorDesc;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public String getErrorDesc() {
        return errorDesc;
    }
    
    public static enum ErrorCode {
        OK(0);

        private int code;

        private ErrorCode(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }

        public static ErrorCode instance(int code) {
            for (ErrorCode ec : values()) {
                if (ec.getCode() == code) {
                    return ec;
                }
            }
            throw new IllegalArgumentException(
                    String.format("out of code: %d", code));
        }
    }
}
