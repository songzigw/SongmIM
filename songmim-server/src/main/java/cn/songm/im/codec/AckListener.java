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
 * 响应事件监听器
 *
 * @author zhangsong
 * @since 0.1, 2016-8-2
 * @version 0.1
 *
 */
public interface AckListener<T> {

    /**
     * 当相应成功时
     * 
     * @param data
     */
    public abstract void onSuccess(T data);

    /**
     * 当产生错误时
     * @param eCode
     * @param eMsg
     */
    public abstract void onError(int eCode, String eMsg);

}
