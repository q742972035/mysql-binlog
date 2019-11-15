package com.github.q742972035.mysql.binlog.expose.event;

import com.github.q742972035.mysql.binlog.expose.build.ExposeConfig;
import com.github.q742972035.mysql.binlog.expose.build.BinLogInfo;
import com.github.q742972035.mysql.binlog.expose.build.ExposeConfig;
import com.github.q742972035.mysql.binlog.expose.event.type.FailureType;

/**
 * 失败事件
 * @program: mysql-binlog-incr-expose
 * @description
 * @author: zy
 * @create: 2019-08-12 11:29
 **/
public class DefaultFailureEventListener implements FailureEventListener {
    private ExposeConfig exposeConfig;

    public DefaultFailureEventListener(ExposeConfig exposeConfig) {
        this.exposeConfig = exposeConfig;
    }

    @Override
    public void onFailureEvent(FailureType failureType, BinLogInfo binLogInfo, Exception e) {
        for (FailureEventListener failureEvent : exposeConfig.getFailureEvents()) {
            failureEvent.onFailureEvent(failureType, binLogInfo, e);
        }
    }
}
