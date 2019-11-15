package com.github.q742972035.mysql.binlog.expose.build;

import com.github.q742972035.mysql.binlog.expose.build.factory.TableElementFactory;
import com.github.q742972035.mysql.binlog.expose.exception.CacheUseException;
import com.github.q742972035.mysql.binlog.expose.type.sql.SqlType;
import com.github.q742972035.mysql.binlog.expose.build.factory.TableElementFactory;
import com.github.q742972035.mysql.binlog.expose.exception.CacheUseException;
import com.github.q742972035.mysql.binlog.expose.type.sql.SqlType;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 每一个dml语句一个实例
 * @program: mysql-binlog-incr-expose
 * @description
 * @author: zy
 * @create: 2019-08-15 11:39
 **/
public class EventTableInfo {

    private String database;

    private String table;

    private List<List<TableElement>> tableElements;

    private SqlType sqlType;

    public EventTableInfo(String database, String table) {
        this.database = database;
        this.table = table;
    }

    public void setSqlType(SqlType sqlType) {
        this.sqlType = sqlType;
    }


    public String getDatabase() {
        return database;
    }

    public String getTable() {
        return table;
    }

    public List<List<TableElement>> getTableElements() {
        return tableElements;
    }

    public SqlType getSqlType() {
        return sqlType;
    }

    public void generateTableElements(List<Long> indexList,List<Serializable[]> rows) throws CacheUseException {
        tableElements = TableElementFactory.createTableElements(database,table,indexList,rows);
    }

    public void generateTableElementsMap(List<Map.Entry<Serializable[],Serializable[]>> rows) throws CacheUseException {
        tableElements = TableElementFactory.createTableElementsMap(database,table,rows);
    }
}
