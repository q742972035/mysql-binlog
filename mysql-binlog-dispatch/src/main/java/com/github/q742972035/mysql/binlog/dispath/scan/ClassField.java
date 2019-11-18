package com.github.q742972035.mysql.binlog.dispath.scan;

import com.github.q742972035.mysql.binlog.expose.utils.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ClassField {
    private static Logger LOGGER = LoggerFactory.getLogger(ClassField.class);
    private Class clazz;

    private Field[] fields;
    private Map<String, Field> fieldNameMap = new HashMap<>();
    private Map<String, Annotation> classFieldAnnotationMap = new HashMap<>();

    /**
     * @param clazz
     * @param modifierFilter
     */
    public ClassField(Class clazz, List<Integer> modifierFilter) {
        this.clazz = clazz;
        List<Field> field = ReflectionUtils.findField(clazz);
        Iterator<Field> iterator = field.iterator();
        while (iterator.hasNext()) {
            Field next = iterator.next();
            for (int modifier : modifierFilter) {
                if (modifier == next.getModifiers()) {
                    iterator.remove();
                    break;
                }
            }
            next.setAccessible(true);
            fieldNameMap.put(next.getName(), next);
        }
        this.fields = field.toArray(new Field[0]);
    }

    public Field[] getFields() {
        return fields;
    }

    public Field findField(String name) {
        return fieldNameMap.get(name);
    }

    public Object newInstance() {
        try {
            Constructor constructor = ReflectionUtils.accessibleConstructor(clazz);
            return constructor.newInstance();
        } catch (Exception e) {
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn(String.format("[ %s ]类初始化出现问题，原因：%s", clazz.getName(), e));
            }
        }
        return null;
    }

    public Class getType(String fieldName) {
        Field field = fieldNameMap.get(fieldName);
        if (field == null) {
            return null;
        }
        return fieldNameMap.get(fieldName).getType();
    }

    private static final Annotation EMPTY_ANNOTATION = () -> null;

    public <T extends Annotation> T getAnnotation(Field field, Class<T> annotationClass) {
        String classFieldName = this.clazz.getName() + "#" + field.getName();
        String annotationClassName = annotationClass.getName();
        Annotation annotation = classFieldAnnotationMap.get(classFieldName + "#" + annotationClassName);
        if (annotation != null) {
            if (annotation == EMPTY_ANNOTATION) {
                return null;
            }
            return (T) annotation;
        }
        T desc = field.getAnnotation(annotationClass);
        if (desc != null) {
            classFieldAnnotationMap.putIfAbsent(classFieldName + "#" + annotationClassName, desc);
        } else {
            classFieldAnnotationMap.putIfAbsent(classFieldName + "#" + annotationClassName, EMPTY_ANNOTATION);
        }
        return getAnnotation(field, annotationClass);
    }

    public Class getClazz() {
        return clazz;
    }
}
