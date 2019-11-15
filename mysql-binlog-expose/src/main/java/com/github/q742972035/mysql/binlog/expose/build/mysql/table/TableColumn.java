package com.github.q742972035.mysql.binlog.expose.build.mysql.table;

/**
 * 数据表的列
 * @program: mysql-binlog-incr-expose
 * @description
 * @author: zy
 * @create: 2019-08-19 16:18
 **/
public class TableColumn {
    protected String fieldName;
    protected Class type;

    public TableColumn(String fieldName, Class type) {
        this.fieldName = fieldName;
        this.type = type;
    }

    public String getFieldName() {
        return fieldName;
    }

    public TableColumn setFieldName(String fieldName) {
        this.fieldName = fieldName;
        return this;
    }

    public Class getType() {
        return type;
    }

    public TableColumn setType(Class type) {
        this.type = type;
        return this;
    }
}
