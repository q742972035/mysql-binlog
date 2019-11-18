package com.github.q742972035.mysql.binlog.dispath.scan;


import com.github.q742972035.mysql.binlog.dispath.annotation.EmptyAnnotation;
import com.github.q742972035.mysql.binlog.dispath.asm.core.LocalVariableTableParameterNameDiscoverer;
import com.github.q742972035.mysql.binlog.dispath.asm.core.ParameterNameDiscoverer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

/**
 * MethodMetadata的实现类
 *
 * @program: mysql-binlog-dispath
 * @description:
 * @author: 张忆
 * @create: 2019-10-09 00:41
 **/
public class SimpleMethodMetadata implements MethodMetadata {
    private static final ParameterNameDiscoverer PARAMETER_NAME_DISCOVERER = new LocalVariableTableParameterNameDiscoverer();

    private Method sourceMethod;

    private List<Annotation> annotations;

    private List<MethodParameter> methodParameters;

    private Map<Class,Annotation> classAnnotationMap = new HashMap<>();

    public SimpleMethodMetadata(Method sourceMethod) {
        this.sourceMethod = sourceMethod;
        this.annotations = Arrays.asList(this.sourceMethod.getDeclaredAnnotations());
        getMethodParameters();
    }

    @Override
    public Class<?> getReturnType() {
        return this.sourceMethod.getReturnType();
    }

    @Override
    public Method getMethod() {
        return this.sourceMethod;
    }

    @Override
    public List<Annotation> getDeclaredAnnotations() {
        return this.annotations;
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        Annotation annotation = classAnnotationMap.get(annotationClass);
        if (annotation == null) {
            T t = this.sourceMethod.getAnnotation(annotationClass);
            if (t==null){
                classAnnotationMap.put(annotationClass, EmptyAnnotation.getInstance());
            }else {
                classAnnotationMap.put(annotationClass, t);
                return t;
            }
        }
        return (T) classAnnotationMap.get(annotationClass);
    }

    @Override
    public List<MethodParameter> getMethodParameters() {
        if (this.methodParameters == null) {
            Class<?>[] parameterTypes = this.sourceMethod.getParameterTypes();
            if (parameterTypes == null || parameterTypes.length == 0) {
                return Collections.emptyList();
            }
            List<MethodParameter> methodParameters = new ArrayList<>(parameterTypes.length);
            String[] parameterNames = PARAMETER_NAME_DISCOVERER.getParameterNames(this.sourceMethod);
            Annotation[][] parameterAnnotations = this.sourceMethod.getParameterAnnotations();
            for (int i = 0; i < parameterTypes.length; i++) {
                methodParameters.add(new MethodParameter(parameterTypes[i], parameterNames[i],parameterAnnotations[i]));
            }
            this.methodParameters = methodParameters;
        }
        return this.methodParameters;
    }

    @Override
    public String getName() {
        return sourceMethod.getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleMethodMetadata that = (SimpleMethodMetadata) o;
        return Objects.equals(sourceMethod, that.sourceMethod);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sourceMethod);
    }

    @Override
    public String toString() {
        return this.sourceMethod.toString();
    }
}
