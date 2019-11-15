package build;

import com.mysql.cj.MysqlType;
import org.junit.Test;
import static org.assertj.core.api.Assertions.*;
/**
 * @program: mysql-binlog-incr-expose
 * @description
 * @author: zy
 * @create: 2019-08-19 15:19
 **/
public class MysqlTypeTest {

    @Test
    public void test(){
        assertThat(MysqlType.getByName("bigint")).isEqualTo(MysqlType.BIGINT);
        assertThat(MysqlType.getByName("bit")).isEqualTo(MysqlType.BIT);
        assertThat(MysqlType.getByName("blob")).isEqualTo(MysqlType.BLOB);
        assertThat(MysqlType.getByName("char")).isEqualTo(MysqlType.CHAR);
        assertThat(MysqlType.getByName("date")).isEqualTo(MysqlType.DATE);
        assertThat(MysqlType.getByName("datetime")).isEqualTo(MysqlType.DATETIME);
        assertThat(MysqlType.getByName("decimal")).isEqualTo(MysqlType.DECIMAL);
        assertThat(MysqlType.getByName("double")).isEqualTo(MysqlType.DOUBLE);
        assertThat(MysqlType.getByName("enum")).isEqualTo(MysqlType.ENUM);
        assertThat(MysqlType.getByName("float")).isEqualTo(MysqlType.FLOAT);
        assertThat(MysqlType.getByName("int")).isEqualTo(MysqlType.INT);
        assertThat(MysqlType.getByName("longtext")).isEqualTo(MysqlType.LONGTEXT);
        assertThat(MysqlType.getByName("smallint")).isEqualTo(MysqlType.SMALLINT);
        assertThat(MysqlType.getByName("text")).isEqualTo(MysqlType.TEXT);
        assertThat(MysqlType.getByName("timestamp")).isEqualTo(MysqlType.TIMESTAMP);
        assertThat(MysqlType.getByName("tinyint")).isEqualTo(MysqlType.TINYINT);
        assertThat(MysqlType.getByName("varchar")).isEqualTo(MysqlType.VARCHAR);

    }
}
