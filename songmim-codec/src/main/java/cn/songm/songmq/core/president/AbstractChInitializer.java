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
package cn.songm.songmq.core.president;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.concurrent.EventExecutorGroup;

/**
 * 抽象的管道初始化器
 *
 * @author zhangsong
 * @since 0.1, 2017-2-25
 * @version 0.1
 * 
 */
public abstract class AbstractChInitializer
        extends ChannelInitializer<SocketChannel> {

    // 消息处理者
    private AbstractMessageHandler handler;
    // DefaultEventExecutorGroup
    private EventExecutorGroup eExeGroup;

    public AbstractChInitializer(AbstractMessageHandler msgHandler) {
        this.handler = msgHandler;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(
                eExeGroup,
                // 消息分发处理
                handler);
    }

    public void shutdownGracefully() {
        if (eExeGroup != null) {
            eExeGroup.shutdownGracefully();
        }
    }
}
