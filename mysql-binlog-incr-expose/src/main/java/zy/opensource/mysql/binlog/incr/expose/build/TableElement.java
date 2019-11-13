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
    // 如果是inserth或者delete 是Class对象，如果是update 是Class[2]数组
    private final Object type;
    // 如果是insert或者delete 是一个obj对象 ，如果是update  是一个Object[2] 数组
    private final Object obj;
    private final MysqlType mysqlType;
    private Columns columns;


    public TableElement(int index, Object type, Object obj, MysqlType mysqlType, Columns columns) {
        this.index = index;
        this.type = type;
        this.obj = obj;
        this.mysqlType = mysqlType;
        this.columns = columns;
    }

    public int getIndex() {
        return index;
    }

    public Object getType() {
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

    @Override
    public String toString() {
        return "TableElement{" +
                "index=" + index +
                ", type=" + getTypeInfo() +
                ", obj=" + getObjInfo() +
                ", mysqlType=" + mysqlType +
                ", columns=" + columns +
                '}';
    }

    private String getTypeInfo() {
        if (type == null) {
            return "null";
        }
        if (type instanceof Class) {
            return ((Class) type).getName();
        } else {
            Class[] classes = (Class[]) type;
            StringBuilder builder = new StringBuilder("[");
            for (int i = 0; i < classes.length; i++) {
                if (classes[i] == null) {
                    builder.append("null");
                } else {
                    builder.append(classes[i].getName());
                }
                if (i != classes.length - 1) {
                    builder.append(" , ");
                }
            }
            builder.append("]");
            return builder.toString();
        }
    }

    private String getObjInfo() {
        if (obj == null) {
            return "null";
        }
        if (!(obj instanceof Object[])) {
            return obj.toString();
        } else {
            Object[] objects = (Object[]) obj;
            StringBuilder builder = new StringBuilder("[");
            for (int i = 0; i < objects.length; i++) {
                if (objects[i]==null){
                    builder.append("null");
                }else {
                    builder.append(objects[i].toString());
                }
                if (i != objects.length - 1) {
                    builder.append(" , ");
                }
            }
            builder.append("]");
            return builder.toString();
        }
    }
}
