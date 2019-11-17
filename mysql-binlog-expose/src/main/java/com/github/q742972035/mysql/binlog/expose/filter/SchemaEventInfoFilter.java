package com.github.q742972035.mysql.binlog.expose.filter;

import com.github.q742972035.mysql.binlog.expose.build.ExposeConfig;
import com.github.q742972035.mysql.binlog.expose.global.Global;
import com.github.q742972035.mysql.binlog.expose.utils.StringUtils;
import com.github.q742972035.mysql.binlog.expose.build.EventInfo;
import com.github.q742972035.mysql.binlog.expose.build.ExposeConfig;
import com.github.q742972035.mysql.binlog.expose.utils.StringUtils;
import com.github.shyiko.mysql.binlog.event.EventData;
import com.github.shyiko.mysql.binlog.event.QueryEventData;
import com.github.shyiko.mysql.binlog.event.TableMapEventData;

public class SchemaEventInfoFilter implements EventInfoFilter{


    @Override
    public boolean filter(ExposeConfig config, EventInfo eventInfo) {
        EventData eventData = eventInfo.getEventData();
        if (eventData instanceof QueryEventData){
            return handlerAndReturnByQueryEventData(config,(QueryEventData) eventData);
        }else if (eventData instanceof TableMapEventData){
            Global.CURRENT_TB.set(((TableMapEventData) eventData).getTable());
        }
        return false;
    }

    private boolean handlerAndReturnByQueryEventData(ExposeConfig config,QueryEventData queryEventData){
        String database = queryEventData.getDatabase();
        String schema = config.getSchema();
        Global.CURRENT_DB.set(queryEventData.getDatabase());
        if (StringUtils.isEmpty(schema)){
            return false;
        }
        // 如果database与schema不一致，说明不需要
        return !database.equals(schema);
    }
}
