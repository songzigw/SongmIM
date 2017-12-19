/*
 * Copyright [2016] [zhangsong <songm.cn>].
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package cn.songm.im.codec;

/**
 * 异常类
 *
 * @author zhangsong
 * @since 0.1, 2016-8-2
 * @version 0.1
 * 
 */
public class IMException extends Exception {

    private static final long serialVersionUID = 5118981894942473582L;

    private ErrorCode errorCode;

    private String errorDesc;

    public IMException(ErrorCode errorCode, String errorDesc) {
        super(errorCode + ":" + errorDesc);
        this.errorCode = errorCode;
        this.errorDesc = errorDesc;
    }

    public IMException(ErrorCode errorCode, String errorDesc,
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
        OK(0),
        /** Token无效 */
        TOKEN_INVALID(1),
        /** Session失效 */
        SESSION_DISABLED(2),
        /** 签名失败 */
        SIGN_FAILURE(3),
        /** 消息来源无效 */
        SOURCE_INVALID(4),
        /** Uid无效 */
        UID_INVALID(5);

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
