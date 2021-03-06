package com.github.q742972035.mysql.binlog.expose.build.factory;

import com.github.q742972035.mysql.binlog.expose.build.ExposeContext;
import com.github.q742972035.mysql.binlog.expose.exception.CacheUseException;
import com.github.q742972035.mysql.binlog.expose.build.TableElement;
import com.github.q742972035.mysql.binlog.expose.build.factory.parse.Index;
import com.github.q742972035.mysql.binlog.expose.build.factory.parse.IndexParse;
import com.github.q742972035.mysql.binlog.expose.build.mysql.hanlder.DefaultConnectionHandler;
import com.github.q742972035.mysql.binlog.expose.build.mysql.sql.SqlAcquire;
import com.github.q742972035.mysql.binlog.expose.build.mysql.sql.SqlFormat;
import com.github.q742972035.mysql.binlog.expose.build.mysql.table.Columns;
import com.github.q742972035.mysql.binlog.expose.utils.CacheUtils;
import com.mysql.cj.MysqlType;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 创建TableElement的工厂
 *
 * @program: mysql-binlog-incr-expose
 * @description
 * @author: zy
 * @create: 2019-08-20 15:17
 **/
public class TableElementFactory {

    private static DefaultConnectionHandler connectionHandler = new DefaultConnectionHandler(ExposeContext.getConfig());

    private static Method EXCUTE_METHOD;

    static {
        try {
            EXCUTE_METHOD = DefaultConnectionHandler.class.getMethod("execute", String.class, Class.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }


    /**
     * 创建了TableElement的半成品，只有index、mysqlType字段
     *
     * @param db
     * @param tableName
     * @return
     */
    public static List<List<TableElement>> createTableElements(String db, String tableName, List<Long> indexList, List<Serializable[]> rows) {
        List<List<TableElement>> list;
        // 获取列信息 (有可能获取到alter后的语句(DDL还没有触发，但是数据库信息已经存在))
        List<Columns> tableColumns = null;
        try {
            tableColumns = (List<Columns>) CacheUtils.cache(new CacheUtils.Info(connectionHandler, EXCUTE_METHOD, SqlAcquire.getSql(SqlFormat.COLUMN_SQL, db, tableName), Columns.class));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        list = new ArrayList<>(rows.size());
        for (int i = 0; i < rows.size(); i++) {
            Serializable[] serializables = rows.get(i);
            IndexParse indexParse = new IndexParse(indexList, serializables);
            Index index = indexParse.index();
            List<TableElement> tableElements = new ArrayList<>(tableColumns.size());
            for (int j = 0; j < tableColumns.size(); j++) {
                Columns columns = tableColumns.get(j);
                if (index != null) {
                    Long longIndex = index.getIndex();
                    Serializable serializable = index.getSerializable();
                    if (j == longIndex.intValue()) {
                        if (serializable != null) {
                            tableElements.add(new TableElement(j + 1, serializable.getClass(), serializable, MysqlType.getByName(columns.getDataType()), columns));
                        } else {
                            tableElements.add(new TableElement(j + 1, null, null, MysqlType.getByName(columns.getDataType()), columns));
                        }
                        index = indexParse.index();
                    } else {
                        tableElements.add(new TableElement(j + 1, null, null, MysqlType.getByName(columns.getDataType()), columns));
                    }
                } else {
                    tableElements.add(new TableElement(j + 1, null, null, MysqlType.getByName(columns.getDataType()), columns));
                }
            }
            list.add(tableElements);
        }
        return list;
    }


    public static List<List<TableElement>> createTableElementsMap(String db, String tableName, List<Map.Entry<Serializable[], Serializable[]>> rows) {
        List<List<TableElement>> list;
        // 获取列信息 (有可能获取到alter后的语句(DDL还没有触发，但是数据库信息已经存在))
        List<Columns> tableColumns = null;
        try {
            tableColumns = (List<Columns>) CacheUtils.cache(new CacheUtils.Info(connectionHandler, EXCUTE_METHOD, SqlAcquire.getSql(SqlFormat.COLUMN_SQL, db, tableName), Columns.class));
        } catch (Exception e) {
            throw new CacheUseException(e);
        }
        list = new ArrayList<>(rows.size());

        for (int i = 0; i < rows.size(); i++) {
            Map.Entry<Serializable[], Serializable[]> entry = rows.get(i);
            List<TableElement> tableElements = new ArrayList<>(tableColumns.size());
            Serializable[] before = entry.getKey();
            Serializable[] after = entry.getValue();

            for (int j = 0; j < tableColumns.size(); j++) {
                Columns columns = tableColumns.get(j);
                Object[] objects = new Object[2];
                objects[0] = before[j];
                objects[1] = after[j];
                Class[] classes = new Class[2];
                if (objects[0] != null) {
                    classes[0] = objects[0].getClass();
                }
                if (objects[1] != null) {
                    classes[1] = objects[1].getClass();
                }
                tableElements.add(new TableElement(j + 1, classes, objects, MysqlType.getByName(columns.getDataType()), columns));
            }
            list.add(tableElements);
        }
        return list;
    }
}
