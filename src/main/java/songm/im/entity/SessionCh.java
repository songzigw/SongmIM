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
package songm.im.entity;

import io.netty.channel.Channel;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import songm.im.operation.Operation.Type;

/**
 * 用户与服务端的会话
 *
 * @author zhangsong
 * @since 0.1, 2016-7-29
 * @version 0.1
 * 
 */
public class SessionCh extends Session {

    private static final long serialVersionUID = -2720707956157888183L;

    private Set<Channel> chSet = new HashSet<Channel>();
    
    public SessionCh(String sessionId, String tokenId, String uid) {
        super(sessionId, tokenId, uid);
    }
    
    public void addCh(Channel ch) {
        chSet.add(ch);
    }
    
    public void removeCh(Channel ch) {
        chSet.remove(ch);
    }

    public void onReceived(byte[] payload) {
        Iterator<Channel> iter = chSet.iterator();
        while (iter.hasNext()) {
            Channel ch = (Channel) iter.next();
            Protocol pro = new Protocol();
            pro.setOperation(Type.MESSAGE.getValue());
            pro.setBody(payload);
            ch.writeAndFlush(pro);
        }
    }

    public void clearCh() {
        Iterator<Channel> iter = chSet.iterator();
        while (iter.hasNext()) {
            Channel ch = (Channel) iter.next();
            ch.close().syncUninterruptibly();
        }
        chSet.clear();
    }
    
}
