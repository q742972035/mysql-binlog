package com.github.q742972035.mysql.binlog.expose.build.mysql.hanlder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * @program: mysql-binlog-incr-expose
 * @description
 * @author: zy
 * @create: 2019-08-19 16:06
 **/
public class DefaultStatmentHandler implements StatementHandler {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private ResultSetHandler resultSetHandler = new DefaultResultSetHandler();

    @Override
    public <T> List<T> excute(Connection connection, String sql, Class<T> type) {
        Statement statement = null;
        try {
            statement = connection.createStatement();
            return resultSetHandler.excute(statement, sql, type);
        } catch (SQLException e) {
            if (logger.isErrorEnabled()) {
                logger.error("创建statement异常,", e);
            }
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    if (logger.isErrorEnabled()) {
                        logger.error("关闭statement异常,", e);
                    }
                }
            }
        }
        return null;
    }

}
