package build.mysql.parse;

import com.github.q742972035.mysql.binlog.incr.expose.build.mysql.parse.SqlParse;
import org.junit.Test;
import static org.assertj.core.api.Assertions.*;
/**
 * @program: mysql-binlog-incr-expose
 * @description
 * @author: zy
 * @create: 2019-08-20 20:45
 **/
public class SqlParseTest {

    @Test
    public void test(){
        String sql = "CREATE TABLE `aaaa`.`test_binlog_inno` (\n" +
                "  `id` int(11) NOT NULL AUTO_INCREMENT,\n" +
                "  `name` varchar(20) DEFAULT NULL,\n" +
                "  `age` int(10) DEFAULT NULL,\n" +
                "  `money` decimal(10,3) DEFAULT NULL,\n" +
                "  `ct` datetime DEFAULT CURRENT_TIMESTAMP,\n" +
                "  `ut` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,\n" +
                "  PRIMARY KEY (`id`)\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";

        SqlParse sqlParse = new SqlParse(sql);

        assertThat(sqlParse.getDababase()).isEqualTo("aaaa");
        assertThat(sqlParse.getTableName()).isEqualTo("test_binlog_inno");


        sql = "create    table    `test_binlog_inno` (\n" +
                "  `id` int(11) NOT NULL AUTO_INCREMENT,\n" +
                "  `name` varchar(20) DEFAULT NULL,\n" +
                "  `age` int(10) DEFAULT NULL,\n" +
                "  `money` decimal(10,3) DEFAULT NULL,\n" +
                "  `ct` datetime DEFAULT CURRENT_TIMESTAMP,\n" +
                "  `ut` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,\n" +
                "  PRIMARY KEY (`id`)\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";

        sqlParse = new SqlParse(sql);

        assertThat(sqlParse.getDababase()).isNull();
        assertThat(sqlParse.getTableName()).isEqualTo("test_binlog_inno");

        sql = "ALTER TABLE `test_alter` MODIFY COLUMN `change`  varchar(11) NULL DEFAULT NULL AFTER `id`, ADD COLUMN `ct`  datetime NULL AFTER `change`;";
        sqlParse = new SqlParse(sql);

        assertThat(sqlParse.getDababase()).isNull();
        assertThat(sqlParse.getTableName()).isEqualTo("test_alter");


        sql = "ALTER TABLE `test_binlog_inno`\n" +
                "CHANGE COLUMN `name` `name1`  varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL AFTER `id`,\n" +
                "CHANGE COLUMN `age` `age1`  varchar(20) NULL DEFAULT NULL AFTER `name1`";
        sqlParse = new SqlParse(sql);

        assertThat(sqlParse.getDababase()).isNull();
        assertThat(sqlParse.getTableName()).isEqualTo("test_binlog_inno");


        sql = "DROP TABLE `test_binlog_inno`";
        sqlParse = new SqlParse(sql);

        assertThat(sqlParse.getDababase()).isNull();
        assertThat(sqlParse.getTableName()).isEqualTo("test_binlog_inno");
    }
}
