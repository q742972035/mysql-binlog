package zy.opensource.mysql.binlog.incr.expose.build.mysql.factory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @program: mysql-binlog-incr-expose
 * @description
 * @author: zy
 * @create: 2019-08-19 16:00
 **/
public class ConnectionFactory {

    public static Connection getConnection(DataSource dataSource) throws SQLException {
        return dataSource.getConnection();
    }
}
