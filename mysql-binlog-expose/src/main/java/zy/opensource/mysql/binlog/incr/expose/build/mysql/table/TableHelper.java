package zy.opensource.mysql.binlog.incr.expose.build.mysql.table;

import zy.opensource.mysql.binlog.incr.expose.utils.ClassUtils;
import zy.opensource.mysql.binlog.incr.expose.utils.ReflectionUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * 帮助Table填充它的元素
 *
 * @program: mysql-binlog-incr-expose
 * @description
 * @author: zy
 * @create: 2019-08-19 17:14
 **/
public class TableHelper {
    private Table table;

    public TableHelper(Table table) {
        this.table = table;
    }

    public void set(int index, Object obj) throws IllegalAccessException, InstantiationException {
        if (obj == null) {
            return;
        }
        TableColumn tableColumn = table.getTableColumns().get(index - 1);
        // 表实例
        Object target = this.table.createTable();
        // 表实例字段
        String fieldName = tableColumn.getFieldName();
        // 表实例字段类型
        Class type = tableColumn.getType();
        Field field = ReflectionUtils.findField(table.getType(), fieldName);
        ReflectionUtils.makeAccessible(field);
        if (type.equals(obj.getClass()) || ClassUtils.isBase(type, obj)) {
            ReflectionUtils.setField(field, target, obj);
            // 类型不一样，需要转换
        } else {
            // 重新查找type，因为他有可能是小写 如long,int等
            type = ClassUtils.resolvePrimitiveIfNecessary(type);
            try {
                Constructor constructor = ReflectionUtils.accessibleConstructor(type, String.class);
                // 查询type的String构造器
                Object o = constructor.newInstance(obj.toString());
                ReflectionUtils.setField(field, target, o);
            } catch (NoSuchMethodException e) {
                // no do...
            } catch (InvocationTargetException e) {
                // no do...
            }
        }
    }
}
