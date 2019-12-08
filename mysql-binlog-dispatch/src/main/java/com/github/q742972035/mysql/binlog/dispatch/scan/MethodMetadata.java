package com.github.q742972035.mysql.binlog.dispatch.scan;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 方法元数据
 */
public interface MethodMetadata {

    /**
     * 方法返回值
     * @return
     */
    Class<?> getReturnType();

    Method getMethod();

    /**
     * 返回注解
     * @return
     */
    List<Annotation> getDeclaredAnnotations();

    <T extends Annotation> T getAnnotation(Class<T> annotationClass);

    List<MethodParameter> getMethodParameters();

    String getName();


}
