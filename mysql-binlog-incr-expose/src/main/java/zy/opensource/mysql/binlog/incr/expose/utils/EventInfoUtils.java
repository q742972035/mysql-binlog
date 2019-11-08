package zy.opensource.mysql.binlog.incr.expose.utils;

import com.github.shyiko.mysql.binlog.event.*;
import zy.opensource.mysql.binlog.incr.expose.build.EventInfo;
import zy.opensource.mysql.binlog.incr.expose.cons.BaseConst;
import zy.opensource.mysql.binlog.incr.expose.type.NextPositionCanUseTypeMatch;
import zy.opensource.mysql.binlog.incr.expose.type.sql.SqlType;

import java.util.List;

/**
 * @program: mysql-binlog-incr-expose
 * @description
 * @author: zy
 * @create: 2019-08-02 17:21
 **/
public class EventInfoUtils {

    /**
     * 返回执行verify的最后一个操作
     */
    public static ThreadLocal<SqlType> RETURN_RESULT = new ThreadLocal<>();

    public static boolean isBase(EventData eventData) {
        return isBaseOne(eventData) || isBaseTwo(eventData);
    }

    public static boolean isBaseOne(EventData eventData) {
        return eventData instanceof RotateEventData;
    }

    public static boolean isBaseTwo(EventData eventData) {
        return eventData instanceof FormatDescriptionEventData;
    }

    public static boolean isEndBase(EventData eventData) {
        return isBaseTwo(eventData);
    }

    public static boolean isNextPositionCanUse(NextPositionCanUseTypeMatch typeMatch, EventInfo eventInfo) {
        return typeMatch.match(eventInfo);
    }


    public static SqlType verifyDDL(EventData eventData) {
        RETURN_RESULT.remove();
        SqlType sqlType;
        if ((sqlType = verifyDDLCreate(eventData)) != null) {
            return sqlType;
        }
        if ((sqlType = verifyDDLAlter(eventData)) != null) {
            return sqlType;
        }
        if ((sqlType = verifyDDLDrop(eventData)) != null) {
            return sqlType;
        }
        if ((sqlType = verifyDDLTruncate(eventData)) != null) {
            return sqlType;
        }
        if (sqlType != null) {
            RETURN_RESULT.set(sqlType);
        }
        return sqlType;
    }

    public static SqlType verifyDDLCreate(EventData eventData) {
        RETURN_RESULT.remove();
        if (!(eventData instanceof QueryEventData)) {
            return null;
        }
        QueryEventData queryEventData = (QueryEventData) eventData;
        String sql = queryEventData.getSql();
        if (StringUtils.compareFormerLower(sql, BaseConst.CREATE_LEN, BaseConst.CREATE_LOW)) {
            RETURN_RESULT.set(SqlType.DDL.CREATE);
            return SqlType.DDL.CREATE;
        }
        return null;
    }

    public static SqlType verifyDDLAlter(EventData eventData) {
        RETURN_RESULT.remove();
        if (!(eventData instanceof QueryEventData)) {
            return null;
        }
        QueryEventData queryEventData = (QueryEventData) eventData;
        String sql = queryEventData.getSql();
        if (StringUtils.compareFormerLower(sql, BaseConst.ALTER_LEN, BaseConst.ALTER_LOW)) {
            RETURN_RESULT.set(SqlType.DDL.ALTER);
            return SqlType.DDL.ALTER;
        }
        return null;
    }

    public static SqlType verifyDDLDrop(EventData eventData) {
        RETURN_RESULT.remove();
        if (!(eventData instanceof QueryEventData)) {
            return null;
        }
        QueryEventData queryEventData = (QueryEventData) eventData;
        String sql = queryEventData.getSql();
        if (StringUtils.compareFormerLower(sql, BaseConst.DROP_LEN, BaseConst.DROP_LOW)) {
            RETURN_RESULT.set(SqlType.DDL.DROP);
            return SqlType.DDL.DROP;
        }
        return null;
    }

    public static SqlType verifyDDLTruncate(EventData eventData) {
        RETURN_RESULT.remove();
        if (!(eventData instanceof QueryEventData)) {
            return null;
        }
        QueryEventData queryEventData = (QueryEventData) eventData;
        String sql = queryEventData.getSql();
        if (StringUtils.compareFormerLower(sql, BaseConst.TRUNCATE_LEN, BaseConst.TRUNCATE_LOW)) {
            RETURN_RESULT.set(SqlType.DDL.TRUNCATE);
            return SqlType.DDL.TRUNCATE;
        }
        return null;
    }

    public static SqlType verifyDMLBegin(EventData eventData) {
        RETURN_RESULT.remove();
        if (!(eventData instanceof QueryEventData)) {
            return null;
        }
        QueryEventData queryEventData = (QueryEventData) eventData;
        String sql = queryEventData.getSql();
        if (BaseConst.BEGIN.equals(sql)) {
            RETURN_RESULT.set(SqlType.DML.BEGIN);
            return SqlType.DML.BEGIN;
        }
        return null;
    }

    public static SqlType verifyDMLEnd(EventData eventData) {
        RETURN_RESULT.remove();
        SqlType sqlType = null;

        if (eventData instanceof XidEventData) {
            sqlType = SqlType.DML.XID;
            RETURN_RESULT.set(sqlType);
            return sqlType;
        }
        if (!(eventData instanceof QueryEventData)) {
            return null;
        }
        QueryEventData queryEventData = (QueryEventData) eventData;
        String sql = queryEventData.getSql();
        if (BaseConst.COMMIT.equals(sql)) {
            sqlType = SqlType.DML.COMMIT;
            RETURN_RESULT.set(sqlType);
        }
        return sqlType;
    }
}
