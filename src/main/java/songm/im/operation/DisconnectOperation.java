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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import songm.im.entity.Protocol;
import songm.im.entity.Session;
import songm.im.service.AuthService;
import songm.im.utils.JsonUtils;

public class DisconnectOperation extends AbstractOperation {

    private final Logger LOG = LoggerFactory
            .getLogger(DisconnectOperation.class);

    @Autowired
    private AuthService authService;

    @Override
    public int operation() {
        return Type.CONN_CLOSE.getValue();
    }

    @Override
    public void action(Channel ch, Protocol pro) {
        Session session = JsonUtils.fromJson(pro.getBody(), Session.class);

        Session newSes = authService.offline(session.getId());
        String json = JsonUtils.toJson(newSes);
        if (json != null) {
            pro.setBody(json.getBytes());
        } else {
            pro.setBody(null);
        }
        ch.writeAndFlush(pro);

        // 关闭连接
        ch.close().syncUninterruptibly();

        LOG.debug("Disconnect succeed for tokenId={}, sessionId={}",
                session.getTokenId(), session.getId());
    }
}
