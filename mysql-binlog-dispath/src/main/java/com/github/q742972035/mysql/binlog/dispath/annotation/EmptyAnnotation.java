package com.github.q742972035.mysql.binlog.dispath.annotation;


import java.lang.annotation.Annotation;

/**
 * 定义空的注解
 */
public class EmptyAnnotation implements Annotation {
    @Override
    public Class<? extends Annotation> annotationType() {
        return null;
    }

    private EmptyAnnotation(){}

    private static final EmptyAnnotation ONLY_ONE = new EmptyAnnotation();

    public static EmptyAnnotation getInstance(){
        return ONLY_ONE;
    }
}
