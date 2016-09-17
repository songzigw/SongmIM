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
import songm.im.server.ChannelLongPolling;
import songm.im.utils.Sequence;
import songm.im.utils.StringUtils;

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
        if (ch instanceof ChannelLongPolling) {
            ChannelLongPolling clp = (ChannelLongPolling) ch;
            if (isFirstConn(clp.getChId())) {
                clp.setChId(Sequence.getInstance().getSequence(7));
                chSet.add(ch);
            }
        } else {
            chSet.add(ch);
        }
    }
    
    public boolean isFirstConn(String chId) {
        if (StringUtils.isEmptyOrNull(chId)) {
            return true;
        }
        if (getChannel(chId) == null) {
            return true;
        }
        return false;
    }
    
    public void removeChannel(Channel ch) {
        if (ch instanceof ChannelLongPolling) {
            ChannelLongPolling clp = (ChannelLongPolling) ch;
            clp.clearMessage();
        } else {
            ch.close().syncUninterruptibly();
        }
        chSet.remove(ch);
    }

    public ChannelLongPolling getChannel(String chId) {
        Iterator<Channel> iter = chSet.iterator();
        while (iter.hasNext()) {
            Channel ch = iter.next();
            if (ch instanceof ChannelLongPolling) {
                ChannelLongPolling clp = (ChannelLongPolling) ch;
                if (clp.getChId().equals(chId)) {
                    return clp;
                }
            }
        }
        return null;
    }
    
    public void onReceived(byte[] payload, Channel out) {
        Iterator<Channel> iter = chSet.iterator();
        while (iter.hasNext()) {
            Channel ch = iter.next();
            if (ch == out) continue;
            if (ch instanceof ChannelLongPolling) {
                ChannelLongPolling clp = (ChannelLongPolling) ch;
                clp.addMessage(payload);
            } else {
                Protocol pro = new Protocol();
                pro.setOperation(Type.MESSAGE.getValue());
                pro.setBody(payload);
                ch.writeAndFlush(pro);
            }
        }
    }

    public void clearChannel() {
        Iterator<Channel> iter = chSet.iterator();
        while (iter.hasNext()) {
            Channel ch = (Channel) iter.next();
            if (ch instanceof ChannelLongPolling) {
                ChannelLongPolling clp = (ChannelLongPolling) ch;
                clp.clearMessage();
            } else {
                ch.close().syncUninterruptibly();
            }
        }
        chSet.clear();
    }
    
    public boolean isChannels() {
        return !chSet.isEmpty();
    }
}
