package build.handler;

import com.alibaba.druid.pool.DruidDataSource;
import com.github.q742972035.mysql.binlog.expose.build.ExposeConfig;
import com.github.q742972035.mysql.binlog.expose.build.mysql.hanlder.DefaultConnectionHandler;
import com.github.q742972035.mysql.binlog.expose.build.mysql.sql.SqlAcquire;
import com.github.q742972035.mysql.binlog.expose.build.mysql.sql.SqlFormat;
import com.github.q742972035.mysql.binlog.expose.build.mysql.table.Columns;
import com.github.q742972035.mysql.binlog.expose.build.mysql.table.Variables;
import com.github.q742972035.mysql.binlog.expose.utils.StreamUtils;
import com.mysql.cj.MysqlType;
import org.junit.Before;
import org.junit.Test;
import utils.BasePropertiesUtils;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;


/**
 * @program: mysql-binlog-incr-expose
 * @description
 * @author: zy
 * @create: 2019-08-20 14:30
 **/
public class DefaultConnectionHandlerTest {
    DruidDataSource dataSource;

    private static final String CREATE_TEST_BINLOG_INNO = "druid/create_test_binlog_inno.sql";
    private static final String CREATE_TEST_BINLOG_MYSIAM = "druid/create_test_binlog_mysiam.sql";

    public String read(String url) throws IOException {
        InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream(url);
        byte[] bytes = StreamUtils.copyToByteArray(resourceAsStream);
        return new String(bytes);
    }

    public void execute(List<String> sqls, DataSource dataSource){
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

    @Before
    public void upset() throws IOException {
        dataSource = new DruidDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl(BasePropertiesUtils.getKey("jdbc.url"));
        dataSource.setUsername(BasePropertiesUtils.getKey("jdbc.username"));
        dataSource.setPassword(BasePropertiesUtils.getKey("jdbc.password"));
    }

    @Test
    public void test() throws IOException {
        ExposeConfig config = new ExposeConfig();
        config.setDataSource(dataSource);
        DefaultConnectionHandler connectionHandler = new DefaultConnectionHandler(config);


        String createDbSql = BasePropertiesUtils.createDbNotExists("t_move");
        String useDb = BasePropertiesUtils.use("t_move");
        String dropTb = BasePropertiesUtils.dropTb("test_binlog_mysiam");
        String createTb = read(CREATE_TEST_BINLOG_MYSIAM);

        execute(Arrays.asList(createDbSql,useDb,dropTb,createTb),dataSource);


        List<Columns> list = connectionHandler.execute(SqlAcquire.getSql(SqlFormat.COLUMN_SQL, "t_move", "test_binlog_mysiam"), Columns.class);


        assertThat(list.size()).isEqualTo(6);
        assertThat(list.get(0).getColumnName()).isEqualTo("id");
        assertThat(list.get(1).getColumnName()).isEqualTo("name");
        assertThat(list.get(2).getColumnName()).isEqualTo("age");
        assertThat(list.get(3).getColumnName()).isEqualTo("money");
        assertThat(list.get(4).getColumnName()).isEqualTo("ct");
        assertThat(list.get(5).getColumnName()).isEqualTo("ut");

        assertThat(MysqlType.getByName(list.get(0).getDataType())).isEqualTo(MysqlType.INT);
        assertThat(MysqlType.getByName(list.get(1).getDataType())).isEqualTo(MysqlType.VARCHAR);
        assertThat(MysqlType.getByName(list.get(2).getDataType())).isEqualTo(MysqlType.INT);
        assertThat(MysqlType.getByName(list.get(3).getDataType())).isEqualTo(MysqlType.DECIMAL);
        assertThat(MysqlType.getByName(list.get(4).getDataType())).isEqualTo(MysqlType.DATETIME);
        assertThat(MysqlType.getByName(list.get(5).getDataType())).isEqualTo(MysqlType.DATETIME);



    }

    @Test
    public void testVariables(){
        ExposeConfig config = new ExposeConfig();
        config.setDataSource(dataSource);
        DefaultConnectionHandler connectionHandler = new DefaultConnectionHandler(config);
        List<Variables> list = connectionHandler.execute(SqlAcquire.getSql(SqlFormat.VARIABLES_SQL, "binlog_format"), Variables.class);
    }
}
