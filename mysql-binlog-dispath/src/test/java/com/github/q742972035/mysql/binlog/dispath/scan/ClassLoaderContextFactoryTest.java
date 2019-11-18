package com.github.q742972035.mysql.binlog.dispath.scan;

import org.assertj.core.api.ThrowableAssert;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

import static org.assertj.core.api.Assertions.*;


public class ClassLoaderContextFactoryTest {

    @Test
    public void testEmptyClass() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        assertThatThrownBy(() -> ClassLoaderContextFactory.createIfNotExist(EmptyClass.class.getName())).hasMessage(EmptyClass.class.getName()+"不持有注解@TableHandler。");
    }





    public static class EmptyClass{

    }
}