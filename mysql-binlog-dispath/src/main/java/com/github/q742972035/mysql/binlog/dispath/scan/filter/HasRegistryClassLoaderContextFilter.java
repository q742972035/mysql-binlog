package com.github.q742972035.mysql.binlog.dispath.scan.filter;


import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class HasRegistryClassLoaderContextFilter implements ClassLoaderContextFilter {

    private static final Set<String> registrar = Collections.synchronizedSet(new HashSet<>());


    @Override
    public boolean beforeCreate(String className) {
        return !registrar.contains(className);
    }

    public static void registry(String className) {
        registrar.add(className);
    }

}
