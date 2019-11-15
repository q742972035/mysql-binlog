package com.github.q742972035.mysql.binlog.expose.event;

import com.github.q742972035.mysql.binlog.expose.build.ExposeConfig;
import com.github.q742972035.mysql.binlog.expose.build.BinLogInfo;
import com.github.q742972035.mysql.binlog.expose.build.ExposeConfig;

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
