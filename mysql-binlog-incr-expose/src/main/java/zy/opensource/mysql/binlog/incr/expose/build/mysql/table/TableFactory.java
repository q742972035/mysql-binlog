package zy.opensource.mysql.binlog.incr.expose.build.mysql.table;

import zy.opensource.mysql.binlog.incr.expose.map.ManagedConcurrentWeakHashMap;

import java.util.Map;

/**
 * @program: mysql-binlog-incr-expose
 * @description
 * @author: zy
 * @create: 2019-08-19 16:50
 **/
public class TableFactory {
    private static final Map<Table, Object> TABLE_OBJECT_MAP = new ManagedConcurrentWeakHashMap<>(128);

    public Object create(Table table) throws IllegalAccessException, InstantiationException {
        Object obj = TABLE_OBJECT_MAP.get(table);
        if (obj == null) {
            synchronized (table) {
                if ((obj = TABLE_OBJECT_MAP.get(table)) == null) {
                    Class objClass = table.getType();
                    obj = objClass.newInstance();
                    TABLE_OBJECT_MAP.put(table,obj);
                }
            }
        }
        return obj;
    }
}
