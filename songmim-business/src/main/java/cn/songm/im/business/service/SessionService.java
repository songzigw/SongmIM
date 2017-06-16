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
package cn.songm.im.business.service;

import cn.songm.im.business.IMException;
import cn.songm.im.business.SessionCh;
import io.netty.channel.Channel;

/**
 * 用户会话管理
 * 
 * @author zhangsong
 * @since 0.1, 2016-8-23
 * @version 0.1
 *
 */
public interface SessionService {

    /**
     * 创建会话
     * 
     * @param tokenId
     * @param sessionId
     * @param ch
     * @return
     */
    public SessionCh createSession(String tokenId, String sessionId, Channel ch) throws IMException;

    /**
     * 获取会话信息
     * 
     * @param sessionId
     * @return
     */
    public SessionCh getSession(String sessionId);

    /**
     * 移除会话
     * 
     * @param sessionId
     * @return
     */
    public SessionCh removeSession(String sessionId);

    /**
     * 移除管道
     * 
     * @param sessionId
     * @param ch
     */
    public void removeChannel(String sessionId, Channel ch) throws IMException;

}
