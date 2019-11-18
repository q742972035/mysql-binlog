package com.github.q742972035.mysql.binlog.dispath.scan;

import com.github.q742972035.mysql.binlog.dispath.annotation.TableHandler;
import com.github.q742972035.mysql.binlog.dispath.annotation.ddl.Alter;
import com.github.q742972035.mysql.binlog.dispath.annotation.ddl.Create;
import com.github.q742972035.mysql.binlog.dispath.annotation.ddl.Drop;
import com.github.q742972035.mysql.binlog.dispath.annotation.dml.*;
import com.github.q742972035.mysql.binlog.dispath.exception.IllegalMethodException;
import com.github.q742972035.mysql.binlog.dispath.exception.IllegalUpdateException;
import com.github.q742972035.mysql.binlog.dispath.exception.MethodAnnotationException;
import com.github.q742972035.mysql.binlog.expose.utils.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 加载类的上下文信息
 *
 * @program: mysql-binlog-dispath
 * @description:
 * @author: 张忆
 * @create: 2019-10-02 03:20
 **/
public class ClassLoaderContext {

    /**
     * 预先定义一些允许getMethodMetadataByAnnotation的annotationClasss
     */
    private static final Set<Class> ALLOW_GET_METHODMETADATA_CLASS = new HashSet<>(Arrays.asList(
            Alter.class, Create.class, Drop.class, Delete.class, Insert.class, Update.class
    ));


    private Class clazz;
    private Object source;

    private static final String BEFORE_CLASS_NAME = Before.class.getName();
    private static final String AFTER_CLASS_NAME = After.class.getName();

    public Class getClazz() {
        return clazz;
    }

    public Object getSource() {
        return source;
    }

    /**
     * 方法->方法元数据的映射
     */
    private Map<Class<? extends Annotation>, MethodMetadata> methodMethodMetadataMap = new HashMap<>();

    ClassLoaderContext() {
    }

    public void init(String className) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        init(className, new SimpleInstanceInitialize());
    }


    public void init(String className, InstanceInitialize instanceInitialize) throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        this.clazz = Class.forName(className);
        // 如果不包含@TableHandler，抛出异常
        if (this.clazz.getAnnotation(TableHandler.class) == null) {
            throw new IllegalStateException(this.clazz.getName() + "不持有注解@TableHandler。");
        }
        this.source = instanceInitialize.instance(this.clazz);
        for (Class allowGetMethodmetadataClass : ALLOW_GET_METHODMETADATA_CLASS) {
            getMethodMetadataByAnnotation(allowGetMethodmetadataClass);
        }
        checkUpdate();
    }

    /**
     * @Update注解的方法必须包含@Before和@After
     */
    private void checkUpdate() {
        MethodMetadata updateMethodMetadata = getMethodMetadataByAnnotation(Update.class);
        if (updateMethodMetadata == null) {
            return;
        }
        // 获取方法的形参
        List<MethodParameter> methodParameters = updateMethodMetadata.getMethodParameters();
        for (MethodParameter methodParameter : methodParameters) {
            List<Annotation> annotations = Arrays.asList(methodParameter.getAnnotations());
            String annoStr = annotations.toString();
            if (!(annoStr.contains(BEFORE_CLASS_NAME) || annoStr.contains(AFTER_CLASS_NAME))) {
                throw new IllegalUpdateException(String.format("%s 不包含@Before和@After", updateMethodMetadata.toString()));
            }
        }

    }

    public MethodMetadata getMethodMetadataByAnnotation(Class<? extends Annotation> annotationClass) {
        if (!ALLOW_GET_METHODMETADATA_CLASS.contains(annotationClass)) {
            throw new IllegalMethodException("只允许使用这些注解:"+ALLOW_GET_METHODMETADATA_CLASS.toString());
        }
        MethodMetadata methodMetadata;
        if ((methodMetadata = methodMethodMetadataMap.get(annotationClass)) != null) {
            return methodMetadata;
        }
        Method[] declaredMethods = ReflectionUtils.getAllDeclaredMethods(clazz);
        String annotationName = annotationClass.getName();
        // 计算符合条件的method数量
        int methodCount = 0;
        for (Method declaredMethod : declaredMethods) {
            Annotation[] annotations = declaredMethod.getAnnotations();
            for (Annotation annotation : annotations) {
                if (annotationName.equals(annotation.toString().replace("@", "").replace("()", ""))) {
                    methodMethodMetadataMap.put(annotationClass, new SimpleMethodMetadata(declaredMethod));
                    if (methodCount++ >= 1) {
                        throw new MethodAnnotationException(annotationName + "的注解方法数量超过1个");
                    }
                    break;
                }
            }
        }

        return methodMethodMetadataMap.get(annotationClass);
    }
}
