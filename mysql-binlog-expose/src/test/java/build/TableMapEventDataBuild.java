package build;

import com.github.shyiko.mysql.binlog.event.TableMapEventData;

import java.util.BitSet;

/**
 * @program: mysql-binlog-incr-expose
 * @description:
 * @author: 张忆
 * @create: 2019-08-03 00:01
 **/
public class TableMapEventDataBuild {
    private TableMapEventData tableMapEventData = new TableMapEventData();
    public TableMapEventData build(){
        return tableMapEventData;
    }

    public TableMapEventDataBuild setTableId(long tableId){
        tableMapEventData.setTableId(tableId);
        return this;
    }

    public TableMapEventDataBuild setDatabase(String database){
        tableMapEventData.setDatabase(database);
        return this;
    }

    public TableMapEventDataBuild setTable(String table){
        tableMapEventData.setTable(table);
        return this;
    }

    public TableMapEventDataBuild setColumnTypes(byte[] columnTypes){
        tableMapEventData.setColumnTypes(columnTypes);
        return this;
    }

    public TableMapEventDataBuild setColumnNullability(BitSet columnNullability){
        tableMapEventData.setColumnNullability(columnNullability);
        return this;
    }
}
