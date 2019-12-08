package com.github.q742972035.mysql.binlog.dispatch.scan;

import com.github.q742972035.mysql.binlog.dispatch.scan.filter.ClassLoaderContextFilter;
import com.github.q742972035.mysql.binlog.dispatch.scan.filter.HasRegistryClassLoaderContextFilter;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassLoaderContextFactory {
    private static final Map<String, ClassLoaderContext> MAP = new HashMap<>();
    /**
     * 记录TableHandler_CLassLoaderContext的映射关系
     */
    public static final Map<String, ClassLoaderContext> TABLE_HANDLER_NAME_CONTEXT_MAP = new HashMap<>();

    /**
     * 注册过滤器
     */
    private static List<ClassLoaderContextFilter> filters = new ArrayList<>();


    static {
        filters.add(new HasRegistryClassLoaderContextFilter());
    }

    public static ClassLoaderContext remove(String className){
        return MAP.remove(className);
    }

    public static ClassLoaderContext createIfNotExist(String className,InstanceInitialize instanceInitialize) throws IllegalAccessException, InstantiationException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException {
        for (ClassLoaderContextFilter filter : filters) {
            if (filter.beforeCreate(className)) {
                return null;
            }
        }
        ClassLoaderContext context;
        if ((context = MAP.get(className)) != null) {
            return context;
        }
        context = new ClassLoaderContext();
        if (MAP.putIfAbsent(className, context) == null) {
            context.init(className,instanceInitialize);
        }
        return MAP.get(className);
    }

    public static ClassLoaderContext createIfNotExist(String className) throws IllegalAccessException, InstantiationException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException {
        for (ClassLoaderContextFilter filter : filters) {
            if (filter.beforeCreate(className)) {
                return null;
            }
        }
        ClassLoaderContext context;
        if ((context = MAP.get(className)) != null) {
            return context;
        }
        context = new ClassLoaderContext();
        if (MAP.putIfAbsent(className, context) == null) {
            context.init(className);
        }
        return MAP.get(className);
    }

    public static ClassLoaderContext getInstance(String tableName){
        return TABLE_HANDLER_NAME_CONTEXT_MAP.get(tableName);
    }
}
