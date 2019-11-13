package zy.opensource.mysql.binlog.incr.expose.filter;

import com.github.shyiko.mysql.binlog.event.EventData;
import com.github.shyiko.mysql.binlog.event.QueryEventData;
import zy.opensource.mysql.binlog.incr.expose.build.EventInfo;
import zy.opensource.mysql.binlog.incr.expose.build.ExposeConfig;
import zy.opensource.mysql.binlog.incr.expose.utils.StringUtils;

public class SchemaEventInfoFilter implements EventInfoFilter{


    @Override
    public boolean filter(ExposeConfig config, EventInfo eventInfo) {
        EventData eventData = eventInfo.getEventData();
        if (eventData instanceof QueryEventData){
            return handlerAndReturnByQueryEventData(config,(QueryEventData) eventData);
        }
        return false;
    }

    private boolean handlerAndReturnByQueryEventData(ExposeConfig config,QueryEventData queryEventData){
        String database = queryEventData.getDatabase();
        String schema = config.getSchema();
        if (StringUtils.isEmpty(schema)){
            return false;
        }
        // 如果database与schema不一致，说明不需要
        return !database.equals(schema);
    }
}
