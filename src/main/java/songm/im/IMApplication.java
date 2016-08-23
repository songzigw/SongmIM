/*
 * Copyright (c) 2016, zhangsong <songm.cn>.
 *
 */

package songm.im;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 单点登入应用
 *
 * @author zhangsong
 * @since 0.1, 2016-8-9
 * @version 0.1
 * 
 */
public class IMApplication implements IMServer {

    private static Logger LOG = LoggerFactory.getLogger(IMApplication.class);

    @Autowired
    private IMServer tcpIMServer;
    @Autowired
    private IMServer wsocketIMServer;

    @Override
    public void start() throws IMException {
        LOG.info("SongMI Server starting");

        tcpIMServer.start();
        wsocketIMServer.start();

        LOG.info("SongMI Server start finish");
    }

    @Override
    public void restart() throws IMException {
        LOG.info("SongMI Server restart...");

        tcpIMServer.restart();
        wsocketIMServer.restart();

        LOG.info("SongMI Server restart finish");
    }

    @Override
    public void shutdown() {
        LOG.info("SongMI Server shutdown...");

        tcpIMServer.shutdown();
        wsocketIMServer.shutdown();

        LOG.info("SongMI Server shutdown finish");
    }
}
