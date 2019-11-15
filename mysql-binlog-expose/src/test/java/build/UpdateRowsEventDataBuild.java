package build;

import com.github.shyiko.mysql.binlog.event.UpdateRowsEventData;

import java.io.Serializable;
import java.util.BitSet;
import java.util.List;
import java.util.Map;

/**
 * @program: mysql-binlog-incr-expose
 * @description
 * @author: zy
 * @create: 2019-08-07 16:02
 **/
public class UpdateRowsEventDataBuild {
    UpdateRowsEventData updateRowsEventData = new UpdateRowsEventData();

    public UpdateRowsEventData build(){
        return updateRowsEventData;
    }

    public UpdateRowsEventDataBuild setIncludedColumns(BitSet includedColumns){
        updateRowsEventData.setIncludedColumns(includedColumns);
        return this;
    }

    public UpdateRowsEventDataBuild setTableId(long tableId){
        updateRowsEventData.setTableId(tableId);
        return this;
    }

    public UpdateRowsEventDataBuild setIncludedColumnsBeforeUpdate(BitSet includedColumnsBeforeUpdate){
        updateRowsEventData.setIncludedColumnsBeforeUpdate(includedColumnsBeforeUpdate);
        return this;
    }

    public UpdateRowsEventDataBuild setRows(List<Map.Entry<Serializable[], Serializable[]>> rows){
        updateRowsEventData.setRows(rows);
        return this;
    }
}
