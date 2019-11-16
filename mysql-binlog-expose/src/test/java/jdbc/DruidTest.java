package jdbc;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.github.q742972035.mysql.binlog.expose.utils.StreamUtils;
import org.junit.Before;
import org.junit.Test;
import utils.BasePropertiesUtils;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

import static utils.BasePropertiesUtils.createDb;
import static utils.BasePropertiesUtils.dropDb;
import static utils.BasePropertiesUtils.dropTb;
import static utils.BasePropertiesUtils.use;

/**
 * @program: mysql-binlog-incr-expose
 * @description
 * @author: zy
 * @create: 2019-08-12 08:49
 **/
public class DruidTest {

    private DruidDataSource dataSource;
    public static final String CREATE_T_TYPE = "druid/create_t_type.sql";

    @Before
    public void upset() throws IOException {
        dataSource = new DruidDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl(BasePropertiesUtils.getKey("jdbc.url"));
        dataSource.setUsername(BasePropertiesUtils.getKey("jdbc.username"));
        dataSource.setPassword(BasePropertiesUtils.getKey("jdbc.password"));
    }


    @Test
    public void testDruid() throws SQLException, IOException {
        String read = read(CREATE_T_TYPE);
        execute(Arrays.asList(dropDb("t_move"),createDb("t_move"),use("t_move"),dropTb("t_type"),read));
    }


    @Test
    public void testColumn() throws SQLException {
        DruidPooledConnection connection = dataSource.getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT COLUMN_NAME,COLUMN_DEFAULT,IS_NULLABLE,DATA_TYPE,COLUMN_TYPE FROM information_schema.`COLUMNS` WHERE TABLE_SCHEMA = 't_move' AND TABLE_NAME = 'test_binlog_mysiam' ORDER BY ORDINAL_POSITION ASC");
        resultSet.next();
    }


    public String read(String url) throws IOException {
        InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream(CREATE_T_TYPE);
        byte[] bytes = StreamUtils.copyToByteArray(resourceAsStream);
        return new String(bytes);
    }


    public void execute(List<String> sqls){
        try (
                Connection connection = dataSource.getConnection();
                Statement statement = connection.createStatement();
        ){
            for (String sql : sqls) {
                statement.addBatch(sql);
            }
            statement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
