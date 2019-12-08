package com.github.q742972035.mysql.binlog.dispatch.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 定义列名
 */
@Target({ElementType.PARAMETER,ElementType.TYPE,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {
    String value() default "";

}
