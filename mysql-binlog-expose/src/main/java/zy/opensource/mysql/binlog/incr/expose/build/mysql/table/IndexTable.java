package zy.opensource.mysql.binlog.incr.expose.build.mysql.table;

import zy.opensource.mysql.binlog.incr.expose.map.ManagedConcurrentWeakHashMap;
import zy.opensource.mysql.binlog.incr.expose.utils.ReflectionUtils;
import zy.opensource.mysql.binlog.incr.expose.utils.StringUtils;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @program: mysql-binlog-incr-expose
 * @description
 * @author: zy
 * @create: 2019-08-19 16:36
 **/
public class IndexTable implements Table {
    private final TableFactory tableFactory = new TableFactory();
    private static final Map<Class, List<IndexTableColumn>> CLASS_INDEXTABLECOLUMNS_MAP = new ManagedConcurrentWeakHashMap<>(64);

    private List<IndexTableColumn> indexTableColumns;
    private Class type;
    private String uuid = StringUtils.createUUID();


    public IndexTable(Class type) {
        this.type = type;
        if ((indexTableColumns = CLASS_INDEXTABLECOLUMNS_MAP.get(type)) != null) {
            return;
        }
        List<Field> fieldList = ReflectionUtils.findField(type);
        indexTableColumns = new ArrayList<>(fieldList.size());
        for (int i = 0; i < fieldList.size(); i++) {
            int index = i + 1;
            indexTableColumns.add(new IndexTableColumn(fieldList.get(i).getName(), fieldList.get(i).getType(), index));
        }
        CLASS_INDEXTABLECOLUMNS_MAP.putIfAbsent(type, indexTableColumns);
    }

    @Override
    public String getId() {
        return uuid;
    }

    @Override
    public List<IndexTableColumn> getTableColumns() {
        return indexTableColumns;
    }

    @Override
    public Class getType() {
        return type;
    }


    @Override
    public Object createTable() throws InstantiationException, IllegalAccessException {
        return tableFactory.create(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IndexTable that = (IndexTable) o;
        return Objects.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}
