package com.github.q742972035.mysql.binlog.expose.event;

import com.github.q742972035.mysql.binlog.expose.build.BaseEventInfoMerge;
import com.github.q742972035.mysql.binlog.expose.build.ExposeConfig;
import com.github.q742972035.mysql.binlog.expose.build.BaseEventInfoMerge;
import com.github.q742972035.mysql.binlog.expose.build.EventInfo;
import com.github.q742972035.mysql.binlog.expose.build.ExposeConfig;
import com.github.shyiko.mysql.binlog.event.EventData;
import com.github.shyiko.mysql.binlog.event.QueryEventData;

/**
 * @program: mysql-binlog-incr-expose
 * @description
 * @author: zy
 * @create: 2019-08-22 08:56
 **/
public class EventInfoExtHandler {
    private ExposeConfig exposeConfig;

    public EventInfoExtHandler(ExposeConfig exposeConfig) {
        this.exposeConfig = exposeConfig;
    }

    public void handlerIfHappenErrorCode(BaseEventInfoMerge baseEventInfoMerge, EventInfo eventInfo) {
        EventData eventData = eventInfo.getEventData();
        if (eventData instanceof QueryEventData) {
            QueryEventData queryEventData = (QueryEventData) eventData;
            int errorCode = queryEventData.getErrorCode();
            if (errorCode != 0) {
                this.exposeConfig.getErrorCodeStrategy().errorCodeHandle(baseEventInfoMerge,eventInfo);
            }
        }
    }

}
