package com.github.q742972035.mysql.binlog.expose.build.strategy;

import com.github.q742972035.mysql.binlog.expose.build.BaseEventInfoMerge;
import com.github.q742972035.mysql.binlog.expose.build.BaseEventInfoMerge;
import com.github.q742972035.mysql.binlog.expose.build.EventInfo;
import com.github.q742972035.mysql.binlog.expose.common.Stringjointer;
import com.github.shyiko.mysql.binlog.event.EventData;
import com.github.shyiko.mysql.binlog.event.QueryEventData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.StringJoiner;

/**
 * 通过log提示errorcode
 *
 * @program: mysql-binlog-incr-expose
 * @description
 * @author: zy
 * @create: 2019-08-21 17:29
 **/
public class WarnErrorCodeStragegy implements ErrorCodeStrategy {
    private static final Logger LOGGER = LoggerFactory.getLogger(WarnErrorCodeStragegy.class);

    @Override
    public void errorCodeHandle(BaseEventInfoMerge lastBaseEventInfoMerge, EventInfo happenErrorEventInfo) {
        Stringjointer stringJoiner = new Stringjointer(",");
        if (lastBaseEventInfoMerge!=null){
            long nextPosition = lastBaseEventInfoMerge.getNextPosition();
            stringJoiner.append("happen error,lastNextPosition is : " + nextPosition);
        }
        EventData eventData = happenErrorEventInfo.getEventData();
        if (eventData instanceof QueryEventData) {
            QueryEventData queryEventData = (QueryEventData) eventData;
            int errorcode = queryEventData.getErrorCode();
            stringJoiner.append("and its errorCode is : " + errorcode);
        }
        LOGGER.warn(stringJoiner.get());
    }
}
