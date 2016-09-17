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
package songm.im.operation;

import io.netty.channel.Channel;

import org.springframework.beans.factory.annotation.Autowired;

import songm.im.Constants;
import songm.im.IMException;
import songm.im.IMException.ErrorCode;
import songm.im.entity.Session;
import songm.im.service.SessionService;

/**
 * 基础操作类
 * @author zhangsong
 *
 */
public abstract class AbstractOperation implements Operation {

    @Autowired
    private SessionService sessionService;

    protected void saveSessionId(Channel ch, String sessionId) {
        ch.attr(Constants.KEY_SESSION_ID).set(sessionId);
    }

    protected Session getSession(Channel ch) {
        String sesId = ch.attr(Constants.KEY_SESSION_ID).get();
        return sessionService.getSession(sesId);
    }

    protected void checkSession(Channel ch) throws IMException {
        if (getSession(ch) == null) {
            throw new IMException(ErrorCode.SESSION_DISABLED,
                    "Session not exist");
        }
    }

}
