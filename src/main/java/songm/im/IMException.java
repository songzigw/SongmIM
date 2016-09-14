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
package songm.im;

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

    private String description;

    public IMException(ErrorCode errorCode, String description) {
        super(errorCode + ":" + description);
        this.errorCode = errorCode;
        this.description = description;
    }

    public IMException(ErrorCode errorCode, String description, Throwable cause) {
        super(errorCode + ":" + description, cause);
        this.errorCode = errorCode;
        this.description = description;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public String getDescription() {
        return description;
    }

    public static enum ErrorCode {
        // 启动异常---------------
        /** 启动错误 */
        START_ERROR,
        
        // Token异常--------------
        /** Token无效 */
        TOKEN_INVALID,
        
        // Session异常------------
        /** Session失效 */
        SESSION_DISABLED,
        
        // MQ异常
        MQ_CONNECT, MQ_PUBLISH, MQ_DISCONNECT
    }

}
