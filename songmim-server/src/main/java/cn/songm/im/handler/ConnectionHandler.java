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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.songm.common.utils.JsonUtils;
import cn.songm.im.IMException;
import cn.songm.im.model.Protocol;
import cn.songm.im.model.Result;
import cn.songm.im.model.Session;
import cn.songm.im.model.SessionCh;
import cn.songm.im.service.TokenService;
import io.netty.channel.Channel;

@Component
public class ConnectionHandler extends AbstractHandler {

    private final Logger LOG = LoggerFactory.getLogger(ConnectionHandler.class);

    @Autowired
    private TokenService authService;

    @Override
    public int operation() {
        return Operation.CONN_AUTH.getValue();
    }

    @Override
    public void action(Channel ch, Protocol pro) {
        Session ses = JsonUtils.fromJson(pro.getBody(), Session.class);

        Result<Session> res = new Result<Session>();
        res.setData(ses);
        try {
            // 连接成功
            SessionCh sesch = authService.online(ses.getTokenId(), ses.getSessionId(), ch);
            saveSessionId(ch, sesch.getSessionId());
            LOG.debug("Connection succeed [tokenId={}, sessionId={}]", sesch.getTokenId(), sesch.getSessionId());

            BeanUtils.copyProperties(sesch, ses);
            ses.setAttribute("ch_id", ch.id().asLongText());
            pro.setBody(JsonUtils.toJsonBytes(res, res.getClass()));
            ch.writeAndFlush(pro);
        } catch (IMException e) {
            // 连接失败
            LOG.debug("Connection failure [tokenId={}, sessionId={}]", ses.getTokenId(), ses.getSessionId());

            res.setErrorCode(e.getErrorCode().name());
            res.setErrorDesc(e.getDescription());
            pro.setBody(JsonUtils.toJsonBytes(res, res.getClass()));
            ch.writeAndFlush(pro);

            // 关闭连接
            ch.close().syncUninterruptibly();
        }
    }

}
