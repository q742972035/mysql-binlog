package zy.opensource.mysql.binlog.incr.expose.build.mysql.table;

import java.util.List;

/**
 * 获取表的信息
 * @program: mysql-binlog-incr-expose
 * @description
 * @author: zy
 * @create: 2019-08-19 16:28
 **/
public interface Table {
    String getId();
    /**
     * 获取一个表的每一个字段信息
     * @return
     */
    List<? extends TableColumn> getTableColumns();

    /**
     * 获取这个表映射到的类
     * @return
     */
    Class getType();


    /**
     * 获取到一个type的单例实例
     * @return
     */
    Object createTable() throws InstantiationException, IllegalAccessException;
}
