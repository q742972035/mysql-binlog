
package utils;

import build.QueryEventDataBuild;
import build.XidEventDataBuild;
import com.github.q742972035.mysql.binlog.incr.expose.cons.BaseConst;
import com.github.q742972035.mysql.binlog.incr.expose.type.sql.SqlType;
import com.github.q742972035.mysql.binlog.incr.expose.utils.EventInfoUtils;
import com.github.shyiko.mysql.binlog.event.QueryEventData;
import com.github.shyiko.mysql.binlog.event.XidEventData;
import org.junit.Test;

import static org.assertj.core.api.Assertions.*;
import static com.github.q742972035.mysql.binlog.incr.expose.utils.EventInfoUtils.RETURN_RESULT;

/**
 * @program: mysql-binlog-incr-expose
 * @description:
 * @author: 张忆
 * @create: 2019-08-03 00:47
 **/

public class EventInfoUtilsTest {

    @Test
    public void verifyDDLTest(){
        SqlType sqlType = EventInfoUtils.verifyDDL(new QueryEventDataBuild().setSql("create ").build());
        assertThat(sqlType).isEqualTo(SqlType.DDL.CREATE);
        assertThat(RETURN_RESULT.get()).isEqualTo(SqlType.DDL.CREATE);

        sqlType = EventInfoUtils.verifyDDL(new QueryEventDataBuild().setSql("alter ").build());
        assertThat(sqlType).isEqualTo(SqlType.DDL.ALTER);
        assertThat(RETURN_RESULT.get()).isEqualTo(SqlType.DDL.ALTER);

        sqlType = EventInfoUtils.verifyDDL(new QueryEventDataBuild().setSql("drop ").build());
        assertThat(sqlType).isEqualTo(SqlType.DDL.DROP);
        assertThat(RETURN_RESULT.get()).isEqualTo(SqlType.DDL.DROP);

        sqlType = EventInfoUtils.verifyDDL(new QueryEventDataBuild().setSql(BaseConst.TRUNCATE_LOW).build());
        assertThat(sqlType).isEqualTo(SqlType.DDL.TRUNCATE);
        assertThat(RETURN_RESULT.get()).isEqualTo(SqlType.DDL.TRUNCATE);


        sqlType = EventInfoUtils.verifyDDL(new XidEventDataBuild().build());
        assertThat(sqlType).isNull();

        sqlType = EventInfoUtils.verifyDDLCreate(new QueryEventDataBuild().setSql("create ").build());
        assertThat(sqlType).isEqualTo(SqlType.DDL.CREATE);
        assertThat(RETURN_RESULT.get()).isEqualTo(SqlType.DDL.CREATE);
        sqlType = EventInfoUtils.verifyDDLCreate(new QueryEventDataBuild().setSql("drop ").build());
        assertThat(sqlType).isNull();
        assertThat(RETURN_RESULT.get()).isNull();
        sqlType = EventInfoUtils.verifyDDLCreate(new QueryEventDataBuild().setSql("alter ").build());
        assertThat(sqlType).isNull();
        assertThat(RETURN_RESULT.get()).isNull();
        sqlType = EventInfoUtils.verifyDDLCreate(new QueryEventDataBuild().setSql(BaseConst.TRUNCATE_LOW).build());
        assertThat(sqlType).isNull();
        assertThat(RETURN_RESULT.get()).isNull();


        sqlType = EventInfoUtils.verifyDDLAlter(new QueryEventDataBuild().setSql("alter ").build());
        assertThat(sqlType).isEqualTo(SqlType.DDL.ALTER);
        assertThat(RETURN_RESULT.get()).isEqualTo(SqlType.DDL.ALTER);
        sqlType = EventInfoUtils.verifyDDLAlter(new QueryEventDataBuild().setSql("create ").build());
        assertThat(sqlType).isNull();
        assertThat(RETURN_RESULT.get()).isNull();
        sqlType = EventInfoUtils.verifyDDLAlter(new QueryEventDataBuild().setSql("drop ").build());
        assertThat(sqlType).isNull();
        assertThat(RETURN_RESULT.get()).isNull();
        sqlType = EventInfoUtils.verifyDDLAlter(new QueryEventDataBuild().setSql(BaseConst.TRUNCATE_LOW).build());
        assertThat(sqlType).isNull();
        assertThat(RETURN_RESULT.get()).isNull();

        sqlType = EventInfoUtils.verifyDDLDrop(new QueryEventDataBuild().setSql("drop ").build());
        assertThat(sqlType).isEqualTo(SqlType.DDL.DROP);
        assertThat(RETURN_RESULT.get()).isEqualTo(SqlType.DDL.DROP);
        sqlType = EventInfoUtils.verifyDDLDrop(new QueryEventDataBuild().setSql("create ").build());
        assertThat(sqlType).isNull();
        assertThat(RETURN_RESULT.get()).isNull();
        sqlType = EventInfoUtils.verifyDDLDrop(new QueryEventDataBuild().setSql("alter ").build());
        assertThat(sqlType).isNull();
        assertThat(RETURN_RESULT.get()).isNull();
        sqlType = EventInfoUtils.verifyDDLDrop(new QueryEventDataBuild().setSql(BaseConst.TRUNCATE_LOW).build());
        assertThat(sqlType).isNull();
        assertThat(RETURN_RESULT.get()).isNull();


        sqlType = EventInfoUtils.verifyDDLTruncate(new QueryEventDataBuild().setSql("drop ").build());
        assertThat(sqlType).isNull();
        assertThat(RETURN_RESULT.get()).isNull();
        sqlType = EventInfoUtils.verifyDDLTruncate(new QueryEventDataBuild().setSql("create ").build());
        assertThat(sqlType).isNull();
        assertThat(RETURN_RESULT.get()).isNull();
        sqlType = EventInfoUtils.verifyDDLTruncate(new QueryEventDataBuild().setSql("alter ").build());
        assertThat(sqlType).isNull();
        assertThat(RETURN_RESULT.get()).isNull();
        sqlType = EventInfoUtils.verifyDDLTruncate(new QueryEventDataBuild().setSql(BaseConst.TRUNCATE_LOW).build());
        assertThat(sqlType).isEqualTo(SqlType.DDL.TRUNCATE);
        assertThat(RETURN_RESULT.get()).isEqualTo(SqlType.DDL.TRUNCATE);
    }


    @Test
    public void verifyDmlTest(){
        SqlType sqlType = EventInfoUtils.verifyDMLBegin(new QueryEventDataBuild().setSql(BaseConst.BEGIN).build());
        assertThat(sqlType).isEqualTo(SqlType.DML.BEGIN);
        assertThat(RETURN_RESULT.get()).isEqualTo(SqlType.DML.BEGIN);

        sqlType = EventInfoUtils.verifyDMLBegin(new QueryEventDataBuild().setSql(BaseConst.COMMIT).build());
        assertThat(sqlType).isNull();
        assertThat(RETURN_RESULT.get()).isNull();
        sqlType = EventInfoUtils.verifyDMLBegin(new XidEventDataBuild().build());
        assertThat(sqlType).isNull();
        assertThat(RETURN_RESULT.get()).isNull();


        // test end
        sqlType = EventInfoUtils.verifyDMLEnd(new QueryEventDataBuild().setSql(BaseConst.COMMIT).build());
        assertThat(sqlType).isEqualTo(SqlType.DML.COMMIT);
        assertThat(RETURN_RESULT.get()).isEqualTo(SqlType.DML.COMMIT);
        sqlType = EventInfoUtils.verifyDMLEnd(new XidEventDataBuild().build());
        assertThat(sqlType).isEqualTo(SqlType.DML.XID);
        assertThat(RETURN_RESULT.get()).isEqualTo(SqlType.DML.XID);
        sqlType = EventInfoUtils.verifyDMLEnd(new QueryEventDataBuild().setSql(BaseConst.ALTER_LOW).build());
        assertThat(sqlType).isNull();
        assertThat(RETURN_RESULT.get()).isNull();


    }


}


