package build;

import com.github.shyiko.mysql.binlog.event.WriteRowsEventData;

import java.io.Serializable;
import java.util.BitSet;
import java.util.List;

/**
 * @program: mysql-binlog-incr-expose
 * @description:
 * @author: 张忆
 * @create: 2019-08-02 23:56
 **/
public class WriteRowsEventDataBuild {
    private WriteRowsEventData writeRowsEventData = new WriteRowsEventData();
    public WriteRowsEventData build(){
        return writeRowsEventData;
    }
    public WriteRowsEventDataBuild setTableId(long tableId){
        writeRowsEventData.setTableId(tableId);
        return this;
    }

    public WriteRowsEventDataBuild setIncludedColumns(BitSet includedColumns){
        writeRowsEventData.setIncludedColumns(includedColumns);
        return this;
    }

    public WriteRowsEventDataBuild setRows(List<Serializable[]> rows){
        writeRowsEventData.setRows(rows);
        return this;
    }
}
