package com.github.q742972035.mysql.binlog.dispath.scan;

import java.lang.reflect.Constructor;

public class SimpleInstanceInitialize implements InstanceInitialize {
    @Override
    public Object instance(Class clazz) {
        try {
            Constructor constructor = clazz.getConstructor();
            return constructor.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
