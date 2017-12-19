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
package cn.songm.im.server.handler;

import org.springframework.beans.factory.annotation.Autowired;

import cn.songm.im.codec.IMException;
import cn.songm.im.codec.IMException.ErrorCode;
import cn.songm.im.codec.Session;
import cn.songm.im.service.SessionService;
import cn.songm.im.service.TokenService;
import cn.songm.songmq.core.president.AbstractMessageHandler;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

/**
 * 基础操作类
 * 
 * @author zhangsong
 *
 */
public abstract class AbstractActioner extends AbstractMessageHandler {

    public static final AttributeKey<Session> KEY_SESSION = AttributeKey
            .valueOf("session");

    @Autowired
    protected TokenService authService;
    @Autowired
    protected SessionService sessionService;

    protected void saveSession(Channel ch, Session session) {
        ch.attr(KEY_SESSION).set(session);
    }

    protected Session getSession(Channel ch) {
        return ch.attr(KEY_SESSION).get();
    }
    
    protected Session checkSession(Channel ch) throws IMException {
        Session ses = ch.attr(KEY_SESSION).get();
        if (ses == null) {
            throw new IMException(ErrorCode.SESSION_DISABLED,
                    "session not exist");
        }
        ses.updateAccess();
        return ses;
    }

    protected void closeConnection(Channel ch) {
        // 关闭连接
        ch.close().syncUninterruptibly();
        // 释放资源
        Session ses = ch.attr(KEY_SESSION).get();
        if (ses != null) {
            sessionService.removeSession(ses.getSessionId());
        }
    }
}
