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
package songm.im.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import songm.im.entity.Session;
import songm.im.entity.Token;
import songm.im.service.SessionService;
import songm.im.utils.Sequence;

@Component
public class SessionServiceImpl implements SessionService {

    private Map<String, Session> sesItems = new HashMap<String, Session>();

    @Override
    public Session create(Token token, String sessionId) {
        Session ses = getSession(sessionId);
        if (ses != null) {
            return ses;
        }

        sessionId = Sequence.getInstance().getSequence(28);
        ses = new Session(sessionId, token);
        sesItems.put(sessionId, ses);

        return ses;
    }

    @Override
    public Session getSession(String sessionId) {
        if (sessionId == null) {
            return null;
        }
        return sesItems.get(sessionId);
    }

    @Override
    public Session remove(String sessionId) {
        return sesItems.remove(sessionId);
    }

    @Override
    public void setAttribute(String sessionId, String name, Object obj) {
        Session ses = getSession(sessionId);
        if (ses == null) {
            return;
        }
        ses.setAttribute(name, obj);
    }

    @Override
    public Object getAttribute(String sessionId, String name) {
        Session ses = getSession(sessionId);
        if (ses == null) {
            return null;
        }
        return ses.getAttribute(name);
    }

}
