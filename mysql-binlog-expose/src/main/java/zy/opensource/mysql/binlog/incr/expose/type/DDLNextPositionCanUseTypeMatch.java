package zy.opensource.mysql.binlog.incr.expose.type;

import com.github.shyiko.mysql.binlog.event.EventData;
import zy.opensource.mysql.binlog.incr.expose.build.EventInfo;
import zy.opensource.mysql.binlog.incr.expose.utils.EventInfoUtils;

/**
 * 只有DDL的nextposition可以使用
 *
 * @program: mysql-binlog-incr-expose
 * @description
 * @author: zy
 * @create: 2019-08-02 17:23
 **/
public class DDLNextPositionCanUseTypeMatch implements NextPositionCanUseTypeMatch {

    @Override
    public boolean match(EventInfo eventInfo) {
        EventData eventData = eventInfo.getEventData();
        if (EventInfoUtils.verifyDDL(eventData)!=null) {
            return true;
        }
        return false;
    }
}
