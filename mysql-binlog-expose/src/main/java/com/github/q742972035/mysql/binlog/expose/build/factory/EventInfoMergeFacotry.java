package com.github.q742972035.mysql.binlog.expose.build.factory;

import com.github.q742972035.mysql.binlog.expose.build.*;
import com.github.q742972035.mysql.binlog.expose.type.sql.SqlType;
import com.github.q742972035.mysql.binlog.expose.build.*;
import com.github.q742972035.mysql.binlog.expose.type.sql.SqlType;

/**
 * @program: mysql-binlog-incr-expose
 * @description
 * @author: zy
 * @create: 2019-08-15 16:19
 **/
public class EventInfoMergeFacotry {

    public static BaseEventInfoMerge createEventInfoMerge(EventInfo eventInfo, ExposeConfig config) {
        // 如果是ddl
        if (eventInfo.getSqlType() instanceof SqlType.DDL) {
            return new DDLEventInfoMerge(config);
        }
        if (eventInfo.getSqlType().equals(SqlType.DML.BEGIN)) {
            return new DMLEventInfoMerge(config);
        }
        return new BaseEventInfoMerge(config);
    }

    public static BaseEventInfoMerge coudleBeNewEventInfoMerge(BaseEventInfoMerge merge, EventInfo eventInfo) {
        if (merge instanceof DMLEventInfoMerge) {
            SqlType sqlType = eventInfo.getSqlType();
            if (sqlType.equals(SqlType.DML.XID)) {
                merge = new XidDMLEventInfoMerge((DMLEventInfoMerge) merge);
            } else if (sqlType.equals(SqlType.DML.COMMIT)) {
                merge = new CommitDMLEventInfoMerge((DMLEventInfoMerge) merge);
            }
        }
        return merge;
    }
}
