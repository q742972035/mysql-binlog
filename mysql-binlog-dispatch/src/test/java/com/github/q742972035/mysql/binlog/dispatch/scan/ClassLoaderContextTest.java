package com.github.q742972035.mysql.binlog.dispatch.scan;

import com.github.q742972035.mysql.binlog.dispatch.annotation.TableHandler;
import com.github.q742972035.mysql.binlog.dispatch.annotation.dml.After;
import com.github.q742972035.mysql.binlog.dispatch.annotation.dml.Before;
import com.github.q742972035.mysql.binlog.dispatch.annotation.dml.Insert;
import com.github.q742972035.mysql.binlog.dispatch.annotation.dml.Update;
import com.github.q742972035.mysql.binlog.dispatch.exception.IllegalUpdateException;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

import static org.assertj.core.api.Assertions.*;

public class ClassLoaderContextTest {


    @Test
    public void testInit() throws IllegalAccessException, InstantiationException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException {
        ClassLoaderContext classLoaderContext = new ClassLoaderContext();
        classLoaderContext.init("zy.opsource.dispath.scan.ClassLoaderContextTest$UserTableHandler");

        Class clazz = classLoaderContext.getClazz();
        Object source = classLoaderContext.getSource();

        assertThat(source.getClass()).isEqualTo(clazz);
        assertThat(clazz).isEqualTo(UserTableHandler.class);
    }

    @Test
    public void testSameAnnotationGt1() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        ClassLoaderContext classLoaderContext = new ClassLoaderContext();

        assertThatThrownBy(() ->
                classLoaderContext.init(SameAnnotationMethodGatherOne.class.getName())
        ).hasMessage(Insert.class.getName() + "的注解方法数量超过1个");
    }


    public static class User {

    }

    @TableHandler(tableName = "user")
    public static class UserTableHandler {

        @Insert
        public void insert(String name) {

        }
    }

    @TableHandler(tableName = "user")
    public static class SameAnnotationMethodGatherOne {

        @Insert
        public void insert1(String name) {

        }

        @Insert
        public void insert2(String name) {

        }
    }



    @Test
    public void testUpdate() throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        ClassLoaderContext classLoaderContext = new ClassLoaderContext();
        classLoaderContext.init("zy.opsource.dispath.scan.ClassLoaderContextTest$UserUpdateTableHandlerEmpty");

        classLoaderContext = new ClassLoaderContext();
        classLoaderContext.init("zy.opsource.dispath.scan.ClassLoaderContextTest$UserUpdateTableHandlerOnePa");

        classLoaderContext = new ClassLoaderContext();
        ClassLoaderContext finalClassLoaderContext = classLoaderContext;
        assertThatExceptionOfType(IllegalUpdateException.class).isThrownBy(()->
                finalClassLoaderContext.init("zy.opsource.dispath.scan.ClassLoaderContextTest$UserUpdateTableHandlerOnePaEmpty")
        );

        classLoaderContext = new ClassLoaderContext();
        classLoaderContext.init("zy.opsource.dispath.scan.ClassLoaderContextTest$UserUpdateTableHandlerTwoPa");

        assertThatExceptionOfType(IllegalUpdateException.class).isThrownBy(()->
                finalClassLoaderContext.init("zy.opsource.dispath.scan.ClassLoaderContextTest$UserUpdateTableHandlerTwoPaAndOneEmpty")
        );
    }


    @Test
    public void testSubClass() throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        ClassLoaderContext classLoaderContext = new ClassLoaderContext();
        classLoaderContext.init("zy.opsource.dispath.scan.ClassLoaderContextTest$SubClass");

        MethodMetadata updateMethod = classLoaderContext.getMethodMetadataByAnnotation(Update.class);
        assertThat(updateMethod).isNotNull();
    }

    @TableHandler(tableName = "user")
    public static class SubClass extends UserUpdateTableHandlerEmpty{
    }

    @TableHandler(tableName = "user")
    public static class UserUpdateTableHandlerEmpty{
        @Update
        public void update() {

        }
    }

    @TableHandler(tableName = "user")
    public static class UserUpdateTableHandlerOnePa{
        @Update
        public void update(@Before String name) {

        }
    }

    @TableHandler(tableName = "user")
    public static class UserUpdateTableHandlerOnePaEmpty{
        @Update
        public void update(String name) {

        }
    }

    @TableHandler(tableName = "user")
    public static class UserUpdateTableHandlerTwoPa{
        @Update
        public void update(@Before String name,@After String aaa) {

        }
    }

    @TableHandler(tableName = "user")
    public static class UserUpdateTableHandlerTwoPaAndOneEmpty{
        @Update
        public void update(@Before String name,String aaa) {

        }
    }
}