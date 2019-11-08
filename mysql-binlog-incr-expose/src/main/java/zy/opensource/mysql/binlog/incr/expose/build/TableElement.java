package zy.opensource.mysql.binlog.incr.expose.build;

import com.mysql.cj.MysqlType;
import zy.opensource.mysql.binlog.incr.expose.build.mysql.table.Columns;

/**
 * 表节点信息
 *
 * @program: mysql-binlog-incr-expose
 * @description
 * @author: zy
 * @create: 2019-08-19 14:43
 **/
public class TableElement {
    private final int index;
    private final Class type;
    // 如果是insert或者delete 是一个obj对象 ，如果是update  是一个Object[2] 数组
    private final Object obj;
    private final MysqlType mysqlType;
    private Columns columns;


    public TableElement(int index, Class type, Object obj, MysqlType mysqlType, Columns columns) {
        this.index = index;
        this.type = type;
        this.obj = obj;
        this.mysqlType = mysqlType;
        this.columns = columns;
    }

    public int getIndex() {
        return index;
    }

    public Class getType() {
        return type;
    }

    public Object getObj() {
        return obj;
    }

    public MysqlType getMysqlType() {
        return mysqlType;
    }

    public Columns getColumns() {
        return columns;
    }
}
