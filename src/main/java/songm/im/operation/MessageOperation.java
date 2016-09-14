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

import songm.im.IMException;
import songm.im.entity.Entity;
import songm.im.entity.Message;
import songm.im.entity.Protocol;
import songm.im.service.ClientService;
import songm.im.utils.JsonUtils;

public class MessageOperation extends AbstractOperation {

    private final Logger LOG = LoggerFactory.getLogger(MessageOperation.class);
    
    @Autowired
    private ClientService clientService;

    @Override
    public int operation() {
        return Type.MSG_SEND.getValue();
    }

    @Override
    public void action(Channel ch, Protocol pro) {
        try {
            checkSession(ch);
        } catch (IMException e) {
            ch.close().syncUninterruptibly();
            return;
        }

        Message msg = JsonUtils.fromJson(pro.getBody(), Message.class);
        clientService.getClient(msg.getFrom()).trigger(pro.getBody(), ch);
        Entity ent = new Entity();
        try {
            clientService.publish(msg.getFrom(), msg.getTo(), pro.getBody());
        } catch (IMException e) {
            ent.setSucceed(false);
            ent.setErrorCode(e.getErrorCode().name());
        }
        LOG.debug("Message send", pro.toString());

        pro.setBody(JsonUtils.toJson(ent, Entity.class).getBytes());
        ch.writeAndFlush(pro);
    }

}
