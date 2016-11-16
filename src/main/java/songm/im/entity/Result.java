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
package songm.im.entity;

import java.io.Serializable;

/**
 * 结果数据包装类
 *
 * @author  zhangsong
 * @since   0.1, 2016-7-29
 * @version 0.1
 * 
 */
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 7055382336733429251L;

    private Boolean succeed;
    
    private String errorCode;
    
    private String errorDesc;
    
    private T data;

    public Result() {
        succeed = true;
    }

    public Boolean getSucceed() {
        return succeed;
    }

    public void setSucceed(Boolean succeed) {
        this.succeed = succeed;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        if (errorCode != null) {
            this.succeed = false;
        }
        this.errorCode = errorCode;
    }
    
    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getErrorDesc() {
        return this.errorDesc;
    }
    
    public void setErrorDesc(String errorDesc) {
        this.errorDesc = errorDesc;
    }
    
    @Override
    public String toString() {
        return "Result [succeed=" + succeed + ", errorCode=" + errorCode + ", errorDesc=" + errorDesc + ", data=" + data + "]";
    }
}
