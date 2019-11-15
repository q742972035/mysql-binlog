package com.github.q742972035.mysql.binlog.expose.build.strategy;

import com.github.q742972035.mysql.binlog.expose.build.BaseEventInfoMerge;
import com.github.q742972035.mysql.binlog.expose.build.BaseEventInfoMerge;
import com.github.q742972035.mysql.binlog.expose.build.EventInfo;

/**
 * 错误码非0策略
 * @program: mysql-binlog-incr-expose
 * @description
 * @author: zy
 * @create: 2019-08-21 17:07
 **/
public interface ErrorCodeStrategy {

    /**
     *
     * @param lastBaseEventInfoMerge 发生错误的上个完整的事务信息(用于获取最后一个nextPosition)
     * @param happenErrorEventInfo 发生错误的happenErrorEventInfo
     */
    void errorCodeHandle(BaseEventInfoMerge lastBaseEventInfoMerge, EventInfo happenErrorEventInfo);
}
