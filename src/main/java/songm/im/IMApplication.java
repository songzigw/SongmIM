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
package songm.im;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 聊天即时消息应用
 *
 * @author zhangsong
 * @since 0.1, 2016-8-9
 * @version 0.1
 * 
 */
public class IMApplication implements IMServer {

    private static Logger LOG = LoggerFactory.getLogger(IMApplication.class);

    private IMServer tcpIMServer;
    private IMServer wsocketIMServer;
    
    private boolean running;

    public void setTcpIMServer(IMServer tcpIMServer) {
        this.tcpIMServer = tcpIMServer;
    }

    public void setWsocketIMServer(IMServer wsocketIMServer) {
        this.wsocketIMServer = wsocketIMServer;
    }
    
    public void init() {
        try {
            this.start();
        } catch (IMException e) {
            LOG.error("SongmIMServer start error", e);
        }
    }

    @Override
    public void start() throws IMException {
        LOG.info("SongIM Server starting");

        tcpIMServer.start();
        wsocketIMServer.start();
        running = true;

        LOG.info("SongIM Server start finish");
    }

    @Override
    public void restart() throws IMException {
        LOG.info("SongIM Server restart...");

        tcpIMServer.restart();
        wsocketIMServer.restart();
        running = true;

        LOG.info("SongIM Server restart finish");
    }

    @Override
    public void shutdown() {
        LOG.info("SongIM Server shutdown...");

        tcpIMServer.shutdown();
        wsocketIMServer.shutdown();
        running = false;

        LOG.info("SongIM Server shutdown finish");
    }

    @Override
    public boolean isRunning() {
        return running;
    }
}
