package zy.opensource.mysql.binlog.incr.expose.build;

import com.github.shyiko.mysql.binlog.event.DeleteRowsEventData;
import com.github.shyiko.mysql.binlog.event.TableMapEventData;
import com.github.shyiko.mysql.binlog.event.UpdateRowsEventData;
import com.github.shyiko.mysql.binlog.event.WriteRowsEventData;
import zy.opensource.mysql.binlog.incr.expose.type.sql.SqlType;
import zy.opensource.mysql.binlog.incr.expose.utils.MathUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Map;

/**
 * @program: mysql-binlog-incr-expose
 * @description
 * @author: zy
 * @create: 2019-08-15 16:12
 **/
public class DMLEventInfoMerge extends BaseEventInfoMerge {


    public DMLEventInfoMerge(ExposeConfig exposeConfig) {
        super(exposeConfig);
    }

    public DMLEventInfoMerge(DMLEventInfoMerge other) {
        super(other);
        this.lastEventTableInfo = other.lastEventTableInfo;
        this.historyEventTableInfos = other.historyEventTableInfos;
    }

    /**
     * 一个dml 语句一个实例
     */
    protected EventTableInfo lastEventTableInfo;

    /**
     * 每一次新增 修改 删除 后都会保存进历史操作记录
     */
    protected List<EventTableInfo> historyEventTableInfos = new ArrayList<>();


    public EventTableInfo getLastEventTableInfo() {
        return lastEventTableInfo;
    }

    public List<EventTableInfo> getHistoryEventTableInfos() {
        return historyEventTableInfos;
    }

    @Override
    protected void beforeMergeForDmlInsert(EventInfo eventInfo) {
        WriteRowsEventData writeRowsEventData = (WriteRowsEventData) eventInfo.getEventData();
        List<Long> indexList = MathUtils.getIndexList(writeRowsEventData.getIncludedColumns());
        List<Serializable[]> rows = writeRowsEventData.getRows();
        lastEventTableInfo.setSqlType(SqlType.DML.INSERT);
        lastEventTableInfo.generateTableElements(indexList,rows);
        historyEventTableInfos.add(lastEventTableInfo);
    }


    @Override
    protected void beforeMergeForDmlUpdate(EventInfo eventInfo) {
        UpdateRowsEventData updateRowsEventData = (UpdateRowsEventData) eventInfo.getEventData();
        List<Map.Entry<Serializable[], Serializable[]>> rows = updateRowsEventData.getRows();
        lastEventTableInfo.setSqlType(SqlType.DML.UPDATE);
        lastEventTableInfo.generateTableElementsMap(rows);
        historyEventTableInfos.add(lastEventTableInfo);
    }

    @Override
    protected void beforeMergeForDmlDelete(EventInfo eventInfo) {
        DeleteRowsEventData deleteRowsEventData = (DeleteRowsEventData) eventInfo.getEventData();
        List<Long> indexList = MathUtils.getIndexList(deleteRowsEventData.getIncludedColumns());
        List<Serializable[]> rows = deleteRowsEventData.getRows();
        lastEventTableInfo.setSqlType(SqlType.DML.DELETE);
        lastEventTableInfo.generateTableElements(indexList,rows);
        historyEventTableInfos.add(lastEventTableInfo);
    }

    @Override
    protected void beforeMergeForDmlTableMap(EventInfo eventInfo) {
        TableMapEventData tableMapEventData = (TableMapEventData) eventInfo.getEventData();
        lastEventTableInfo = new EventTableInfo(tableMapEventData.getDatabase(), tableMapEventData.getTable());
    }
}
