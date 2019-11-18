package com.github.q742972035.mysql.binlog.dispath.cache;

import com.github.q742972035.mysql.binlog.dispath.scan.ClassField;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassFieldCache {

    private static final Map<Class, ClassField> MAP = new HashMap<>();
    private static final List<Integer> MODIFIER_FILTER = Arrays.asList(Modifier.FINAL, Modifier.STATIC);

    public static synchronized void put(Class clazz) {
        MAP.putIfAbsent(clazz, new ClassField(clazz, MODIFIER_FILTER));
    }

    public static ClassField getAndSet(Class clazz) {
        ClassField classField = MAP.get(clazz);
        if (classField == null) {
            put(clazz);
        }
        return MAP.get(clazz);
    }

}
