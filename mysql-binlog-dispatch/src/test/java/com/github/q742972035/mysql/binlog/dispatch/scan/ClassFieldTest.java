package com.github.q742972035.mysql.binlog.dispatch.scan;


import com.github.q742972035.mysql.binlog.dispatch.scan.dto.User;
import org.assertj.core.util.Lists;
import org.junit.Test;

import java.lang.reflect.Modifier;

import static org.assertj.core.api.Assertions.*;

public class ClassFieldTest {

    @Test
    public void test() throws NoSuchFieldException {
        ClassField classField = new ClassField(User.class, Lists.newArrayList(Modifier.FINAL,Modifier.STATIC));
        assertThat(classField.getFields().length).isEqualTo(6);

        assertThat(classField.findField(User.class.getDeclaredField("id").getName())).isNotNull();
        assertThat(classField.findField(User.class.getDeclaredField("username").getName())).isNotNull();
        assertThat(classField.findField(User.class.getDeclaredField("email").getName())).isNotNull();
        assertThat(classField.findField(User.class.getDeclaredField("phone").getName())).isNotNull();
        assertThat(classField.findField(User.class.getDeclaredField("create_time").getName())).isNotNull();
        assertThat(classField.findField(User.class.getDeclaredField("update_time").getName())).isNotNull();
    }

}
