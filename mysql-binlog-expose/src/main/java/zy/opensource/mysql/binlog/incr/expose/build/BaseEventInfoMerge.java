package zy.opensource.mysql.binlog.incr.expose.build;

import com.github.shyiko.mysql.binlog.event.*;
import zy.opensource.mysql.binlog.incr.expose.exception.EventMergeException;
import zy.opensource.mysql.binlog.incr.expose.filter.EventInfoFilter;
import zy.opensource.mysql.binlog.incr.expose.type.sql.SqlType;
import zy.opensource.mysql.binlog.incr.expose.utils.CollectionUtils;
import zy.opensource.mysql.binlog.incr.expose.utils.EventInfoUtils;
import zy.opensource.mysql.binlog.incr.expose.utils.ReflectionUtils;

import java.util.ArrayList;
import java.util.List;

import static zy.opensource.mysql.binlog.incr.expose.utils.EventInfoUtils.RETURN_RESULT;


/**
 * @program: mysql-binlog-incr-expose
 * @description:
 * @author: 张忆
 * @create: 2019-08-03 00:18
 **/
public class BaseEventInfoMerge {

    public BaseEventInfoMerge(ExposeConfig exposeConfig) {
        this.exposeConfig = exposeConfig;
    }

    public BaseEventInfoMerge(BaseEventInfoMerge other) {
        this.sqlTypes = other.sqlTypes;
        this.usefulSqlTypes = other.usefulSqlTypes;
        this.exposeConfig = other.exposeConfig;
        this.eventInfos = other.eventInfos;
        this.isCapping = other.isCapping;
        this.isPreCapping = other.isPreCapping;
        this.nextPosition = other.nextPosition;
        this.eventInfoWrap = other.eventInfoWrap;
    }

    /**
     * 记录进来过的sqlType
     */
    private List<SqlType> sqlTypes = new ArrayList<>();

    /**
     * 记录是有使用价值的sqltype
     */
    private List<SqlType> usefulSqlTypes = new ArrayList<>();

    protected ExposeConfig exposeConfig;


    /**
     * 记录同一条SQL语句内的EventInfo
     */
    List<EventInfo> eventInfos = new ArrayList<>();

    /**
     * 标记是否封顶
     */
    private boolean isCapping;
    protected boolean isPreCapping;

    private long nextPosition;
    private long currentPosition;


    public long getNextPosition() {
        return nextPosition;
    }

    public long getCurrentPosition() {
        return currentPosition;
    }

    public BaseEventInfoMerge setCurrentPosition(long currentPosition) {
        this.currentPosition = currentPosition;
        return this;
    }

    /**
     * 包装eventInfoWrap
     */
    private EventInfoWrap eventInfoWrap;

    public boolean merge(EventInfo eventInfo) throws EventMergeException {
        if (isCapping) {
            return false;
        }
        // 过滤eventInfo
        List<EventInfoFilter> eventInfoFilters = exposeConfig.getEventInfoFilter();
        if (!CollectionUtils.isEmpty(eventInfoFilters)){
            for (EventInfoFilter eventInfoFilter : eventInfoFilters) {
                if (eventInfoFilter.filter(exposeConfig,eventInfo)){
                    return false;
                }
            }
        }
        if (beforeMerge(eventInfo)){
            eventInfos.add(eventInfo);
            if (isPreCapping) {
                isCapping = true;
            }
            nextPosition = eventInfo.getNextPosition();
        }
        return true;
    }


    private List<EventInfo> getEventInfos() {
        return eventInfos;
    }

    public int size() {
        return eventInfos.size();
    }

    public boolean canMerge() {
        return !isCapping;
    }


    protected void beforeMergeForFormatdDscription(EventInfo eventInfo) {
    }


    protected void beforeMergeForDdlCreate(EventInfo eventInfo) {
    }

    protected void beforeMergeForDdlAlter(EventInfo eventInfo) {
    }

    protected void beforeMergeForDdlDrop(EventInfo eventInfo) {
    }

    protected void beforeMergeForDdlTruncate(EventInfo eventInfo) {
    }


    protected void beforeMergeForDmlInsert(EventInfo eventInfo) {
    }

    protected void beforeMergeForDmlUpdate(EventInfo eventInfo) {
    }

    protected void beforeMergeForDmlDelete(EventInfo eventInfo) {
    }

    protected void beforeMergeForDmlXid(EventInfo eventInfo) {
    }

    protected void beforeMergeForDmlCommit(EventInfo eventInfo) {
    }

    protected void beforeMergeForDmlTableMap(EventInfo eventInfo) {
    }


    /**
     * 合并前处理,允许合并则返回true
     */
    private boolean beforeMerge(EventInfo eventInfo) throws EventMergeException {
        EventData eventData = eventInfo.getEventData();
        if (sqlTypes.size() == 0) {
            if (EventInfoUtils.isBaseOne(eventData)) {
                sqlTypes.add(SqlType.BaseInfo.ROTATE);
                // 如果是DDL 直接封顶
                return true;
            } else if (EventInfoUtils.verifyDDL(eventData) != null) {
                sqlTypes.add(RETURN_RESULT.get());
                usefulSqlTypes.add(RETURN_RESULT.get());

                if (RETURN_RESULT.get().equals(SqlType.DDL.CREATE)) {
                    beforeMergeForDdlCreate(eventInfo);
                } else if (RETURN_RESULT.get().equals(SqlType.DDL.ALTER)) {
                    beforeMergeForDdlAlter(eventInfo);
                } else if (RETURN_RESULT.get().equals(SqlType.DDL.DROP)) {
                    beforeMergeForDdlDrop(eventInfo);
                } else if (RETURN_RESULT.get().equals(SqlType.DDL.TRUNCATE)) {
                    beforeMergeForDdlTruncate(eventInfo);
                }
                isPreCapping = true;
                return true;
                // 如果遇到begin开头，这有可能是一个事务
            } else if (EventInfoUtils.verifyDMLBegin(eventData) != null) {
                sqlTypes.add(SqlType.DML.BEGIN);
                return true;
            }
        } else {
            int size = sqlTypes.size();
            if (size == 1) {
                if (sqlTypes.get(0).equals(SqlType.BaseInfo.ROTATE)) {
                    if (EventInfoUtils.isBaseTwo(eventData)) {
                        sqlTypes.add(SqlType.BaseInfo.FORMATD_ESCRIPTION);
                        usefulSqlTypes.add(SqlType.BaseInfo.FORMATD_ESCRIPTION);
                        beforeMergeForFormatdDscription(eventInfo);
                        // 进行预封顶
                        isPreCapping = true;
                        return true;
                    } else {
                        throw new EventMergeException("基础信息的第二次event必须是[ " + SqlType.BASE_FORMATD_ESCRIPTION + " ]");
                    }
                }
                if (eventData instanceof TableMapEventData) {
                    sqlTypes.add(SqlType.DML.TABLE_MAP);
                    beforeMergeForDmlTableMap(eventInfo);
                    return true;
                }
            } else if (sqlTypes.get(0).equals(SqlType.DML.BEGIN)) {
                // 插入前，大小如果是偶数，并且当前不是最后，而最后一个sqlType不是TABLE_MAP
                if (size % 2 == 0 && EventInfoUtils.verifyDMLEnd(eventData) == null && !sqlTypes.get(size - 1).equals(SqlType.DML.TABLE_MAP)) {
                    throw new EventMergeException("格式不正确，第[ " + size + " ]个event 应该是[ " + SqlType.DML_TABLE_MAP + " ]");
                }
                if (RETURN_RESULT.get() != null) {
                    // 进行预封顶
                    sqlTypes.add(RETURN_RESULT.get());
                    isPreCapping = true;
                    return true;
                }
                if (eventData instanceof WriteRowsEventData) {
                    sqlTypes.add(SqlType.DML.INSERT);
                    usefulSqlTypes.add(SqlType.DML.INSERT);
                    beforeMergeForDmlInsert(eventInfo);
                    return true;
                } else if (eventData instanceof UpdateRowsEventData) {
                    sqlTypes.add(SqlType.DML.UPDATE);
                    usefulSqlTypes.add(SqlType.DML.UPDATE);
                    beforeMergeForDmlUpdate(eventInfo);
                    return true;
                } else if (eventData instanceof DeleteRowsEventData) {
                    sqlTypes.add(SqlType.DML.DELETE);
                    usefulSqlTypes.add(SqlType.DML.DELETE);
                    beforeMergeForDmlDelete(eventInfo);
                    return true;
                } else if (eventData instanceof TableMapEventData) {
                    sqlTypes.add(SqlType.DML.TABLE_MAP);
                    beforeMergeForDmlTableMap(eventInfo);
                    return true;
                } else if (EventInfoUtils.verifyDMLEnd(eventData) != null) {
                    // 进行预封顶
                    sqlTypes.add(RETURN_RESULT.get());
                    isPreCapping = true;

                    if (RETURN_RESULT.get().equals(SqlType.DML.XID)) {
                        beforeMergeForDmlXid(eventInfo);
                    } else if (RETURN_RESULT.get().equals(SqlType.DML.COMMIT)) {
                        beforeMergeForDmlCommit(eventInfo);
                    }
                    return true;
                } else {
                    String format = String.format("格式不正确，插入的第%s个event,只能是[ %s %s %s %s %s %s]", size + 1, SqlType.DML_INSERT, SqlType.DML_UPDATE, SqlType.DML_DELETE, SqlType.DML_TABLE_MAP, SqlType.DML_COMMIT, SqlType.DML_XID);
                    throw new EventMergeException(format);
                }
            }
        }
        return false;
    }


    public EventInfoWrap parse() {
        if (eventInfoWrap == null) {
            eventInfoWrap = new EventInfoWrap(this);
        }
        return eventInfoWrap;
    }
}
