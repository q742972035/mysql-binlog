package com.github.q742972035.mysql.binlog.dispath.list;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class GenerateGroupPrefixName {

    static Map<String, AtomicInteger> map = new ConcurrentHashMap<>();

    public static String generator(Object object) {
        String className = object.getClass().getName();
        AtomicInteger atomicInteger = map.get(className);
        if (atomicInteger == null) {
            map.putIfAbsent(className, new AtomicInteger());
            atomicInteger = map.get(className);
        }
        return className + "#" + atomicInteger.getAndIncrement() + "-";
    }
}
