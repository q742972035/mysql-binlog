package build;

import com.github.shyiko.mysql.binlog.event.DeleteRowsEventData;

import java.io.Serializable;
import java.util.BitSet;
import java.util.List;

/**
 * @program: mysql-binlog-incr-expose
 * @description
 * @author: zy
 * @create: 2019-08-07 16:23
 **/
public class DeleteRowsEventDataBuild {
    private DeleteRowsEventData deleteRowsEventData = new DeleteRowsEventData();
    public DeleteRowsEventData build(){
        return deleteRowsEventData;
    }

    public DeleteRowsEventDataBuild setTableId(long tableId){
        deleteRowsEventData.setTableId(tableId);
        return this;
    }

    public DeleteRowsEventDataBuild setIncludedColumns(BitSet includedColumns){
        deleteRowsEventData.setIncludedColumns(includedColumns);
        return this;
    }

    public DeleteRowsEventDataBuild setRows(List<Serializable[]> rows){
        deleteRowsEventData.setRows(rows);
        return this;
    }
}
