package zy.opensource.mysql.binlog.incr.expose.build.mysql.hanlder;

import zy.opensource.mysql.binlog.incr.expose.build.mysql.table.Table;

import java.sql.Connection;
import java.util.List;

/**
 * @program: mysql-binlog-incr-expose
 * @description
 * @author: zy
 * @create: 2019-08-19 16:07
 **/
public interface StatementHandler {
    <T >List<T> excute(Connection connection, String sql, Class<T> type);
}
