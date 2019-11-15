package com.github.q742972035.mysql.binlog.expose.build.mysql.table;

/**
 * @program: mysql-binlog-incr-expose
 * @description
 * @author: zy
 * @create: 2019-08-19 16:20
 **/
public class IndexTableColumn extends TableColumn {
    protected int index;

    public IndexTableColumn(String fieldName, Class type, int index) {
        super(fieldName, type);
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public IndexTableColumn setIndex(int index) {
        this.index = index;
        return this;
    }
}
