package com.github.q742972035.mysql.binlog.expose.type;

import com.github.q742972035.mysql.binlog.expose.build.EventInfo;
import com.github.q742972035.mysql.binlog.expose.build.EventInfo;
import com.github.shyiko.mysql.binlog.event.EventData;
import com.github.shyiko.mysql.binlog.event.QueryEventData;
import com.github.shyiko.mysql.binlog.event.XidEventData;

/**
 * 只有DML的nextposition可以使用
 * @program: mysql-binlog-incr-expose
 * @description
 * @author: zy
 * @create: 2019-08-02 17:23
 **/
public class DMLNextPositionCanUseTypeMatch implements NextPositionCanUseTypeMatch{
    private static final String COMMIT = "COMMIT".toLowerCase();
    private static final int COMMIT_LEN = COMMIT.length();

    @Override
    public boolean match(EventInfo eventInfo) {
        EventData eventData = eventInfo.getEventData();
        if (eventData instanceof XidEventData){
            return true;
        }
        if (eventData instanceof QueryEventData){
            QueryEventData queryEventData = (QueryEventData) eventData;
            String sql = queryEventData.getSql();
            if (sql != null && sql.length() >= COMMIT_LEN && sql.substring(0, COMMIT_LEN).toLowerCase().equals(COMMIT)){
                return true;
            }
        }
        if (eventData instanceof XidEventData){
            return true;
        }
        return false;
    }
}
