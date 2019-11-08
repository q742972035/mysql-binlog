package zy.opensource.mysql.binlog.incr.expose.build.mysql.hanlder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zy.opensource.mysql.binlog.incr.expose.build.mysql.table.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: mysql-binlog-incr-expose
 * @description:
 * @author: 张忆
 * @create: 2019-08-20 02:13
 **/
public class DefaultResultSetHandler implements ResultSetHandler {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public <T> List<T> excute(Statement statement, String sql, Class<T> type) {
        ResultSet resultSet = null;
        try {
            resultSet = statement.executeQuery(sql);
            return doExcute(resultSet, type);
        } catch (SQLException e) {
            logger.error("创建resultSet异常,", e);
        } catch (IllegalAccessException e) {
            logger.error("", e);
        } catch (InstantiationException e) {
            logger.error("", e);
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    logger.error("关闭resultSet异常,", e);
                }
            }
        }
        return null;
    }

    private <T> List<T> doExcute(ResultSet resultSet, Class<T> type) throws SQLException, InstantiationException, IllegalAccessException {
        List<T> list = new ArrayList<>();
        while (resultSet.next()) {
            Table table = new IndexTable(type);
            TableHelper helper = new TableHelper(table);
            List<? extends TableColumn> tableColumns = table.getTableColumns();
            for (TableColumn tableColumn : tableColumns) {
                if (tableColumn instanceof IndexTableColumn) {
                    IndexTableColumn itc = (IndexTableColumn) tableColumn;
                    helper.set(itc.getIndex(), resultSet.getObject(itc.getIndex()));
                }
            }
            list.add((T) table.createTable());
        }
        return list;
    }

}
