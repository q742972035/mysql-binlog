package com.github.q742972035.mysql.binlog.expose.build;

import com.github.q742972035.mysql.binlog.expose.cons.BaseConst;
import com.github.q742972035.mysql.binlog.expose.type.DDLNextPositionCanUseTypeMatch;
import com.github.q742972035.mysql.binlog.expose.type.sql.SqlType;
import com.github.q742972035.mysql.binlog.expose.utils.EventInfoUtils;
import com.github.q742972035.mysql.binlog.expose.cons.BaseConst;
import com.github.q742972035.mysql.binlog.expose.exception.EventInfoCreateException;
import com.github.q742972035.mysql.binlog.expose.type.DDLNextPositionCanUseTypeMatch;
import com.github.q742972035.mysql.binlog.expose.type.DMLNextPositionCanUseTypeMatch;
import com.github.q742972035.mysql.binlog.expose.type.NextPositionCanUseTypeMatch;
import com.github.q742972035.mysql.binlog.expose.type.sql.SqlType;
import com.github.q742972035.mysql.binlog.expose.utils.EventInfoUtils;
import com.github.shyiko.mysql.binlog.event.*;

import java.util.Arrays;
import java.util.List;


/**
 * @program: mysql-binlog-incr-expose
 * @description
 * @author: zy
 * @create: 2019-08-02 16:53
 **/
public class EventInfo {
    final EventHeader eventHeader;
    final EventData eventData;
    long nextPosition = -1;
    final SqlType sqlType;
    /**
     * 定义nextPosition是否能够使用的匹配规则
     */
    final List<NextPositionCanUseTypeMatch> typeMatchs = Arrays.asList(new DMLNextPositionCanUseTypeMatch(), new DDLNextPositionCanUseTypeMatch());

    public EventInfo(Event event) throws EventInfoCreateException {
        this.eventHeader = event.getHeader();
        this.eventData = event.getData();
        if (this.eventHeader instanceof EventHeaderV4) {
            EventHeaderV4 headerV4 = (EventHeaderV4) eventHeader;
            nextPosition = headerV4.getNextPosition();
        }
        if (eventData instanceof RotateEventData) {
            sqlType = SqlType.BaseInfo.ROTATE;
        } else if (eventData instanceof FormatDescriptionEventData) {
            sqlType = SqlType.BaseInfo.FORMATD_ESCRIPTION;
        } else if (eventData instanceof QueryEventData) {
            String sql = ((QueryEventData) eventData).getSql();
            if (sql.toLowerCase().startsWith(BaseConst.CREATE_LOW)) {
                sqlType = SqlType.DDL.CREATE;
            } else if (sql.toLowerCase().startsWith(BaseConst.ALTER_LOW)) {
                sqlType = SqlType.DDL.ALTER;
            } else if (sql.toLowerCase().startsWith(BaseConst.DROP_LOW)) {
                sqlType = SqlType.DDL.DROP;
            } else if (sql.toLowerCase().startsWith(BaseConst.TRUNCATE_LOW)) {
                sqlType = SqlType.DDL.TRUNCATE;
            } else if (BaseConst.BEGIN.equals(sql)) {
                sqlType = SqlType.DML.BEGIN;
            } else if (BaseConst.COMMIT.equals(sql)) {
                sqlType = SqlType.DML.COMMIT;
            } else {
                throw new EventInfoCreateException();
            }
        } else if (eventData instanceof XidEventData) {
            sqlType = SqlType.DML.XID;
        } else if (eventData instanceof TableMapEventData) {
            sqlType = SqlType.DML.TABLE_MAP;
        } else if (eventData instanceof WriteRowsEventData) {
            sqlType = SqlType.DML.INSERT;
        } else if (eventData instanceof UpdateRowsEventData) {
            sqlType = SqlType.DML.UPDATE;
        } else if (eventData instanceof DeleteRowsEventData) {
            sqlType = SqlType.DML.DELETE;
        } else {
            throw new EventInfoCreateException();
        }
    }

    public SqlType getSqlType() {
        return sqlType;
    }

    public EventData getEventData() {
        return eventData;
    }

    public EventHeader getEventHeader() {
        return eventHeader;
    }

    public EventType getEventType() {
        return eventHeader.getEventType();
    }

    public long getNextPosition() {
        return nextPosition;
    }

    /**
     * nextPosition 是否能够作为binlogpositions
     *
     * @return
     */
    public boolean isNextPositionCanUse() {
        if (-1 == nextPosition) {
            return false;
        }
        for (NextPositionCanUseTypeMatch typeMatch : typeMatchs) {
            if (EventInfoUtils.isNextPositionCanUse(typeMatch, this)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "EventInfo{" +
                "eventHeader=" + eventHeader +
                ", eventData=" + eventData +
                ", sqlType=" + sqlType +
                '}';
    }
}
