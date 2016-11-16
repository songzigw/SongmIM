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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import songm.im.IMException;
import songm.im.entity.Conversation;
import songm.im.entity.Message;
import songm.im.entity.Protocol;
import songm.im.entity.Result;
import songm.im.mqtt.ClientUser;
import songm.im.service.ClientService;
import songm.im.utils.JsonUtils;

@Component
public class MessageHandler extends AbstractHandler {

    private final Logger LOG = LoggerFactory.getLogger(MessageHandler.class);
    
    @Autowired
    private ClientService clientService;

    @Override
    public int operation() {
        return Operation.MSG_SEND.getValue();
    }

    @Override
    public void action(Channel ch, Protocol pro) throws IMException {
        checkSession(ch);

        Message msg = JsonUtils.fromJson(pro.getBody(), Message.class);
        ClientUser cUser = clientService.getClient(msg.getFrom());
        cUser.trigger(pro.getBody(), ch);
        cUser.publish(Conversation.Type.instance(msg.getConv()), msg.getTo(), pro.getBody());
        LOG.debug("MessageHandler {}", pro.toString());

        Result<Message> res = new Result<Message>();
        res.setData(msg);
        pro.setBody(JsonUtils.toJsonBytes(res, res.getClass()));
        ch.writeAndFlush(pro);
    }

}
