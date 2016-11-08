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
package songm.im.handler;

import io.netty.channel.Channel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import songm.im.IMException;
import songm.im.entity.Protocol;
import songm.im.entity.Session;
import songm.im.service.AuthService;

@Component
public class DisconnectHandler extends AbstractHandler {

    private final Logger LOG = LoggerFactory.getLogger(DisconnectHandler.class);

    @Autowired
    private AuthService authService;

    @Override
    public int operation() {
        return Operation.CONN_CLOSE.getValue();
    }

    @Override
    public void action(Channel ch, Protocol pro) throws IMException {
        // 关闭连接
        ch.close().syncUninterruptibly();

        Session session = this.getSession(ch);
        if (session == null) {
            return;
        }

        authService.offline(session.getSessionId());
        LOG.debug("DisconnectHandler tokenId={}, sessionId={}",
                session.getTokenId(), session.getSessionId());
    }
}
