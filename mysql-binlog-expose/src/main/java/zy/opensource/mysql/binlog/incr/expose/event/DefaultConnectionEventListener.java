package zy.opensource.mysql.binlog.incr.expose.event;

import zy.opensource.mysql.binlog.incr.expose.build.ExposeConfig;
import zy.opensource.mysql.binlog.incr.expose.build.BinLogInfo;

/**
 * @program: mysql-binlog-incr-expose
 * @description
 * @author: zy
 * @create: 2019-08-12 12:12
 **/
public class DefaultConnectionEventListener implements ConnectionEventListener {
    private ExposeConfig exposeConfig;

    public DefaultConnectionEventListener(ExposeConfig exposeConfig) {
        this.exposeConfig = exposeConfig;
    }

    @Override
    public void onConnect(boolean connected, BinLogInfo binLogInfo) {
        for (ConnectionEventListener connectionEvent : exposeConfig.getConnectionEvents()) {
            connectionEvent.onConnect(connected, binLogInfo);
        }
    }
}
