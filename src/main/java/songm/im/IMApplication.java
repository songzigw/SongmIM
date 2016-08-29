/*
 * Copyright (c) 2016, zhangsong <songm.cn>.
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
