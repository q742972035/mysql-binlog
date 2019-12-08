package com.github.q742972035.mysql.binlog.dispatch.scan;

import com.github.q742972035.mysql.binlog.dispatch.annotation.Column;
import com.github.q742972035.mysql.binlog.dispatch.scan.dto.User;
import com.github.q742972035.mysql.binlog.expose.build.TableElement;
import com.github.q742972035.mysql.binlog.expose.build.mysql.table.Columns;
import com.mysql.cj.MysqlType;
import org.junit.Test;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class ObjectBindTest {
    @Test
    public void testoneParameter() throws NoSuchMethodException, NoSuchFieldException {
        MethodMetadata methodMetadata = new SimpleMethodMetadata(TestClass.class.getMethod("oneParameter", Date.class));
        Object[] bind = ObjectBind.bind(methodMetadata, get(), -1, -1);

        assertThat(bind[0].getClass()).isEqualTo(Date.class);
        assertThat(bind[0]).isEqualTo(get().get(4).getObj());



        methodMetadata = new SimpleMethodMetadata(TestClass.class.getMethod("oneParameter1", Date.class));
        bind = ObjectBind.bind(methodMetadata, get(), -1, -1);
        assertThat(bind.length).isEqualTo(1);
        assertThat(bind[0]).isNull();

        methodMetadata = new SimpleMethodMetadata(TestClass.class.getMethod("sameNameNotSameType", User.class));
        bind = ObjectBind.bind(methodMetadata, get(), -1, -1);

        assertThat(bind[0].getClass()).isEqualTo(User.class);
        assertUser((User) bind[0],get());

        methodMetadata = new SimpleMethodMetadata(TestClass.class.getMethod("sameNameSameType", String.class));
        bind = ObjectBind.bind(methodMetadata, get(), -1, -1);

        assertThat(bind[0]).isEqualTo(get().get(1).getObj());

        methodMetadata = new SimpleMethodMetadata(TestClass.class.getMethod("sameNameWithAllType", User.class, String.class));
        bind = ObjectBind.bind(methodMetadata, get(), -1, -1);

        assertThat(bind.length).isEqualTo(2);
        assertThat(bind[0].getClass()).isEqualTo(User.class);
        assertThat(bind[1].getClass()).isEqualTo(String.class);

        assertUser((User) bind[0],get());
        assertThat(bind[1]).isEqualTo(get().get(1).getObj());
    }

    @Test
    public void setObject() throws NoSuchFieldException, NoSuchMethodException {
        MethodMetadata methodMetadata = new SimpleMethodMetadata(ObjectClass.class.getMethod("object", Object.class));
        Object[] bind = ObjectBind.bind(methodMetadata, get(), -1, -1);
    }


    public static class ObjectClass{
        public void oneParameter(Date create_time){

        }

        public void object(Object t){

        }

    }


    public static class TestClass{
        public void oneParameter(Date create_time){

        }

        public void oneParameter1(Date create_time1){

        }

        public void sameNameNotSameType(User username){

        }

        public void sameNameSameType(String username){

        }

        public void sameNameWithAllType(User username,@Column("username") String un){

        }
    }


    private void assertUser(User user,List<TableElement> tableElements){
        assertThat(user.getId()).isEqualTo(tableElements.get(0).getObj());
        assertThat(user.getUsername()).isEqualTo(tableElements.get(1).getObj());
        assertThat(user.getEmail()).isEqualTo(tableElements.get(2).getObj());
        assertThat(user.getPhone()).isEqualTo(tableElements.get(3).getObj());
        assertThat(user.getCreateTime()).isEqualTo(tableElements.get(4).getObj());
        assertThat(user.getUpdateTime()).isEqualTo(tableElements.get(5).getObj());
    }

    private List<TableElement> get() throws NoSuchFieldException {
        TableElement idTe = new TableElement(1, Long.class, 1L, MysqlType.BIGINT, getName("id"));
        TableElement unTe = new TableElement(2, String.class, "用户名", MysqlType.VARCHAR, getName("username"));
        TableElement eTe = new TableElement(3, String.class, "123@qq.com", MysqlType.VARCHAR, getName("email"));
        TableElement pTe = new TableElement(4, String.class, "137665", MysqlType.VARCHAR, getName("phone"));
        TableElement ctTe = new TableElement(5, Date.class, getHour(10), MysqlType.TIMESTAMP, getName("create_time"));
        TableElement utTe = new TableElement(6, Date.class, getHour(14), MysqlType.DATETIME, getName("update_time"));
        return Arrays.asList(idTe,unTe,eTe,pTe,ctTe,utTe);
    }

    private Date getHour(int hour){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,hour);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        return calendar.getTime();
    }

    private Columns getName(String name) {
        Columns columns = new Columns();
        columns.setColumnName(name);
        return columns;
    }
}