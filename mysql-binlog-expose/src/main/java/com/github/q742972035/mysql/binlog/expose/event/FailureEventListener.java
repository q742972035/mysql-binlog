package com.github.q742972035.mysql.binlog.expose.event;


import com.github.q742972035.mysql.binlog.expose.build.BinLogInfo;
import com.github.q742972035.mysql.binlog.expose.event.type.FailureType;

/**
 * 失败事件
 * @program: mysql-binlog-incr-expose
 * @description
 * @author: zy
 * @create: 2019-08-12 11:29
 **/
public interface FailureEventListener {
    void onFailureEvent(FailureType failureType, BinLogInfo binLogInfo, Exception e);
}
