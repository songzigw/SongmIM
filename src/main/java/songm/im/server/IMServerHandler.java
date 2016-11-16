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
package songm.im.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import songm.im.IMException;
import songm.im.entity.Protocol;
import songm.im.handler.Handler;
import songm.im.handler.HandlerManager;
import songm.im.utils.JsonUtils;
import songm.im.entity.Result;

/**
 * 服务器消息处理者
 * 
 * @author zhangsong
 *
 */
@Component
@ChannelHandler.Sharable
public class IMServerHandler extends SimpleChannelInboundHandler<Protocol> {

    private static final Logger LOG = LoggerFactory.getLogger(IMServerHandler.class);

    @Autowired
    private HandlerManager operationManager;

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Protocol pro) throws Exception {
        Handler op = operationManager.find(pro.getOperation());
        if (op != null) {
            try {
                op.action(ctx.channel(), pro);
            } catch (IMException e) {
                Result<Object> res = new Result<Object>();
                res.setErrorCode(e.getErrorCode().name(), e.getDescription());
                pro.setBody(JsonUtils.toJsonBytes(res, res.getClass()));
                ctx.writeAndFlush(pro);
                // ctx.close().syncUninterruptibly();
            }
        } else {
            LOG.warn("Not found operation: " + pro.getOperation());
        }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        LOG.debug("HandlerRemoved", ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOG.error("ExceptionCaught", cause);
    }
}
