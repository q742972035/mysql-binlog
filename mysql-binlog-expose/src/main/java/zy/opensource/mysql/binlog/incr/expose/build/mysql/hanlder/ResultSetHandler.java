package zy.opensource.mysql.binlog.incr.expose.build.mysql.hanlder;

import zy.opensource.mysql.binlog.incr.expose.build.mysql.table.Table;

import java.sql.Statement;
import java.util.List;

public interface ResultSetHandler {
    <T> List<T> excute(Statement statement, String sql, Class<T> type);
}
