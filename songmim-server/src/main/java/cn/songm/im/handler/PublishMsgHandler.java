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
package cn.songm.im.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.songm.common.utils.JsonUtils;
import cn.songm.im.IMException;
import cn.songm.im.model.Conversation.Type;
import cn.songm.im.model.Protocol;
import cn.songm.im.model.Result;
import cn.songm.im.model.Session;
import cn.songm.im.model.message.Message;
import cn.songm.im.mqueue.ClientUser;
import cn.songm.im.service.ClientService;
import io.netty.channel.Channel;

@Component
public class PublishMsgHandler extends AbstractHandler {

    private final Logger LOG = LoggerFactory.getLogger(PublishMsgHandler.class);
    
    @Autowired
    private ClientService clientService;

    @Override
    public int operation() {
        return Operation.PUBLISH_MSG.getValue();
    }

    @Override
    public void action(Channel ch, Protocol pro) throws IMException {
        checkSession(ch);

        Message msg = JsonUtils.fromJson(pro.getBody(), Message.class);
        Session session = this.getSession(ch);
        ClientUser cUser = clientService.getClient(session.getUid());
        cUser.publish(Type.instance(msg.getConv()), msg.getFrom(), pro.getBody());
        cUser.publish(Type.instance(msg.getConv()), msg.getTo(), pro.getBody());
        LOG.debug("[PublishMsgHand: {}]", pro.toString());

        Result<Message> res = new Result<Message>();
        res.setData(msg);
        pro.setBody(JsonUtils.toJsonBytes(res, res.getClass()));
        ch.writeAndFlush(pro);
    }

}
