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
 * @author  zhangsong
 * @since   0.1, 2016-8-23
 * @version 0.1
 *
 */
public interface SessionService {

    public Session getSession(String sessionId);

    public Session create( Token token, String sessionId);

    public Session remove(String sessionId);

    public void setAttribute(String sessionId, String name, Object obj);

    public Object getAttribute(String sessionId, String name);
}
