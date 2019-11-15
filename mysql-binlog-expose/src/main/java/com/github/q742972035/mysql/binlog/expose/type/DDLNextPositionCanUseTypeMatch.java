package com.github.q742972035.mysql.binlog.expose.type;

import com.github.q742972035.mysql.binlog.expose.build.EventInfo;
import com.github.q742972035.mysql.binlog.expose.utils.EventInfoUtils;
import com.github.shyiko.mysql.binlog.event.EventData;

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
