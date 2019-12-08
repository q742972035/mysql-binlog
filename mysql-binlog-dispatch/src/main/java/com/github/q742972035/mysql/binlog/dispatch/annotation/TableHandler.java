package com.github.q742972035.mysql.binlog.dispatch.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于类注解
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface TableHandler {

    /**
     * 表名
     * @return
     */
    String tableName() ;
}
