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
package songm.im.service;

import songm.im.entity.Session;
import songm.im.entity.Token;

/**
 * 用户会话管理
 * 
 * @author zhangsong
 * @since 0.1, 2016-8-23
 * @version 0.1
 *
 */
public interface SessionService {

    /**
     * 获取会话信息
     * 
     * @param sessionId
     * @return
     */
    public Session getSession(String sessionId);

    /**
     * 创建会话
     * 
     * @param token
     * @param sessionId
     * @return
     */
    public Session create(Token token, String sessionId);

    /**
     * 移除会话
     * 
     * @param sessionId
     * @return
     */
    public Session remove(String sessionId);

    /**
     * 在会话中设置属性
     * 
     * @param sessionId
     * @param name
     * @param obj
     */
    public void setAttribute(String sessionId, String name, Object obj);

    /**
     * 获取会话中的属性
     * 
     * @param sessionId
     * @param name
     * @return
     */
    public Object getAttribute(String sessionId, String name);
}
