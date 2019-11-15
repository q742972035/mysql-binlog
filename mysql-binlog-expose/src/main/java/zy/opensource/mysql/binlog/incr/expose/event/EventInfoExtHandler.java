package zy.opensource.mysql.binlog.incr.expose.event;

import com.github.shyiko.mysql.binlog.event.EventData;
import com.github.shyiko.mysql.binlog.event.QueryEventData;
import zy.opensource.mysql.binlog.incr.expose.build.BaseEventInfoMerge;
import zy.opensource.mysql.binlog.incr.expose.build.EventInfo;
import zy.opensource.mysql.binlog.incr.expose.build.ExposeConfig;

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
