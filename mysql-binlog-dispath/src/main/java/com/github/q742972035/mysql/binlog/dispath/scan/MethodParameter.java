package com.github.q742972035.mysql.binlog.dispath.scan;

import java.lang.annotation.Annotation;

/**
 * 方法参数的信息
 * @program: mysql-binlog-dispath
 * @description:
 * @author: 张忆
 * @create: 2019-10-09 00:50
 **/
public class MethodParameter {

    private Class clazz;
    private String name;
    private Annotation[] annotations;

    public MethodParameter(Class clazz, String name,Annotation[] annotations) {
        this.clazz = clazz;
        this.name = name;
        this.annotations = annotations;
    }

    public Class getClazz() {
        return clazz;
    }

    public String getName() {
        return name;
    }

    public Annotation[] getAnnotations() {
        return annotations;
    }
}
