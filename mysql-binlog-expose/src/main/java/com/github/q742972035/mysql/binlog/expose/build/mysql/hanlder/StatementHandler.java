package com.github.q742972035.mysql.binlog.expose.build.mysql.hanlder;


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
