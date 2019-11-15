package com.github.q742972035.mysql.binlog.expose.build.mysql.hanlder;


import java.sql.Statement;
import java.util.List;

public interface ResultSetHandler {
    <T> List<T> excute(Statement statement, String sql, Class<T> type);
}
