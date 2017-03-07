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
package cn.songm.im.mqueue;

import cn.songm.im.IMException;
import cn.songm.im.model.Conversation;
import cn.songm.im.model.SessionCh;
import io.netty.channel.Channel;

/**
 * 客户端用户
 * 
 * @author zhangsong
 *
 */
public interface ClientUser {

    /**
     * 添加Session
     * 
     * @param session
     */
    public void addSession(SessionCh session);

    /**
     * 移除Session
     * 
     * @param session
     */
    public void removeSession(SessionCh session);

    /**
     * 触发消息事件
     * 
     * @param payload
     * @param out
     */
    public void trigger(byte[] payload, Channel out);

    /**
     * 是否包含Session
     * 
     * @return
     */
    public boolean isSessions();

    /**
     * 清除Session
     */
    public SessionCh[] clearSessions();

    /**
     * 发布消息
     * 
     * @param conv
     * @param to
     * @param body
     * @throws IMException
     */
    public void publish(Conversation.Type convType, String id, byte[] body)
            throws IMException;

    /**
     * 订阅主题
     * 
     * @param convType
     * @param id
     */
    public void subscribe(Conversation.Type convType, String id);
    
    /**
     * 取消订阅
     * 
     * @param convType
     * @param id
     */
    public void unsubscribe(Conversation.Type convType, String id);
}
