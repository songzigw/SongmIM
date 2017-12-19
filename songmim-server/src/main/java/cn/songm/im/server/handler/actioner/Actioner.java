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
package cn.songm.im.server.handler.actioner;

import cn.songm.im.codec.Protocol;
import cn.songm.im.codec.Session;
import io.netty.channel.Channel;

/**
 * 业务动作
 * 
 * @author zhangsong
 *
 */
public interface Actioner {

    public int operation();

    void action(Channel ch, Protocol pro, Session session);
}
