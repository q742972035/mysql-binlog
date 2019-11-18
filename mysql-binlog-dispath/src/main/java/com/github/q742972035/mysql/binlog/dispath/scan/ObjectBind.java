package com.github.q742972035.mysql.binlog.dispath.scan;

import com.github.q742972035.mysql.binlog.dispath.annotation.Column;
import com.github.q742972035.mysql.binlog.dispath.annotation.CurrentPosition;
import com.github.q742972035.mysql.binlog.dispath.annotation.NextPosition;
import com.github.q742972035.mysql.binlog.dispath.annotation.dml.After;
import com.github.q742972035.mysql.binlog.dispath.annotation.dml.Before;
import com.github.q742972035.mysql.binlog.dispath.cache.ClassFieldCache;
import com.github.q742972035.mysql.binlog.dispath.transform.ObjectTransform;
import com.github.q742972035.mysql.binlog.dispath.transform.TransformHolder;
import com.github.q742972035.mysql.binlog.expose.build.TableElement;
import com.github.q742972035.mysql.binlog.expose.utils.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 绑定类型
 */
public class ObjectBind {

    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectBind.class);


    static {
        try {
            Class.forName(TransformHolder.class.getName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    /**
     * 保存方法形参的关系
     */
    private static Map<MethodParamKey, MethodParamValue> MAP = new ConcurrentHashMap<>(256);

    public static Object[] bind(MethodMetadata methodMetadata, List<TableElement> tableElements, long currentPosition, long nextPosition) {
        // 获取方法的形参类型、名字、注解[]
        List<MethodParameter> methodParameters = methodMetadata.getMethodParameters();
        if (methodParameters.size() == 0) {
            return null;
        }
        Object[] array = new Object[methodParameters.size()];
        Map<String, Object> columnNameMap = getStringObjectMap(tableElements);
        setArray(methodMetadata, methodParameters, array, columnNameMap, currentPosition, nextPosition);
        return array;
    }


    private static final ThreadLocal<ClassField> CLASS_FIELD_THREAD_LOCAL = new ThreadLocal<>();
    private static final ThreadLocal<Map<String, Object>> MAP_THREAD_LOCAL = new ThreadLocal<>();
    private static final ThreadLocal<MethodParamValue> METHOD_PARAM_VALUE_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * @param methodMetadata
     * @param methodParameters
     * @param array
     * @param columnNameMap    数据中字段名称映射实体
     */
    private static void setArray(MethodMetadata methodMetadata, List<MethodParameter> methodParameters, Object[] array, Map<String, Object> columnNameMap, long currentPosition, long nextPosition) {
        for (int i = 0; i < array.length; i++) {
            // 方法参数的类型、名字以及注解
            MethodParameter methodParameter = methodParameters.get(i);
            // 获取方法参数类型
            Class parameterClazz = methodParameter.getClazz();
            // 方法形参相关属性，如别名等
            MethodParamValue methodParamValue = get(new MethodParamKey(methodMetadata, i), methodParameter);


            boolean b;
            // 如果是@CurrentPosition或者@NextPosition
            if (b = methodParamValue.isCurrentPosition) {
                array[i] = getObject(parameterClazz,currentPosition);
            } else if (b = methodParamValue.isNextPosition) {
                array[i] = getObject(parameterClazz,nextPosition);
            }

            if (b) {
                continue;
            }


            CLASS_FIELD_THREAD_LOCAL.set(ClassFieldCache.getAndSet(parameterClazz));
            MAP_THREAD_LOCAL.set(columnNameMap);
            METHOD_PARAM_VALUE_THREAD_LOCAL.set(methodParamValue);


            Object obj = columnNameMap.get(methodParamValue.name);
            // 如果根据形参名字获取不到实例，说明形参名字与数据库字段名字不统一
            if (obj == null) {
                // 处理如Map<String,Object>的情况
                if (parameterClazz == Map.class) {
                    Map<String, Object> map = get(columnNameMap, methodParamValue.isBefore);
                    array[i] = map;
                    // 处理如自定义类的情况
                } else {
                    ClassField classField = ClassFieldCache.getAndSet(parameterClazz);
                    // 如果obj==null 而且存在转换类型的key
                    ObjectTransform transform = TransformHolder.getTransform(classField.getClazz());
                    if (transform != null) {
                        array[i] = null;
                        continue;
                    }
                    // 具体的
                    array[i] = get(classField, columnNameMap, methodParamValue.isBefore);
                }
                // 处理如具体实体类的情况
                continue;
            }

            // 如果方法参数
            if (methodParamValue.isBefore != null && obj instanceof Object[]) {
                Object[] objs = (Object[]) obj;
                Object before = objs[0];
                Object after = objs[1];

                if (methodParamValue.isBefore && before != null) {
                    objs[0] = getObject(parameterClazz, before);
                    array[i] = objs[0];
                } else if (!methodParamValue.isBefore && after != null) {
                    objs[1] = getObject(parameterClazz, after);
                    array[i] = objs[1];
                }
            } else {
                obj = getObject(parameterClazz, obj);
                array[i] = obj;
            }


            CLASS_FIELD_THREAD_LOCAL.remove();
            MAP_THREAD_LOCAL.remove();
            METHOD_PARAM_VALUE_THREAD_LOCAL.remove();
        }
    }

    private static Object get(ClassField classField, Map<String, Object> columnNameMap, Boolean isBefore) {
        Object instance = classField.newInstance();
        Field[] fields = classField.getFields();
        for (Field field : fields) {
            String name = field.getName();
            // 获取Column
            Column column = classField.getAnnotation(field, Column.class);
            if (column != null && !"".equals(column.value())) {
                name = column.value();
            } else {
                javax.persistence.Column column1 = classField.getAnnotation(field, javax.persistence.Column.class);
                if (column1 != null) {
                    name = column1.name();
                }
            }

            // 获取字段的类
            Class fieldClass = field.getType();
            Object o = columnNameMap.get(name);
            if (o == null) {
                continue;
            }

            if (isBefore == null) {
                ReflectionUtils.setField(field, instance, getObject(fieldClass, o));
            } else {
                Object[] objs = (Object[]) o;
                if (isBefore) {
                    ReflectionUtils.setField(field, instance, getObject(fieldClass, objs[0]));
                } else {
                    ReflectionUtils.setField(field, instance, getObject(fieldClass, objs[1]));
                }
            }

        }
        return instance;
    }

    private static Map<String, Object> get(Map<String, Object> columnNameMap, Boolean isBefore) {
        Map<String, Object> map = new HashMap<>();
        // 没有@Before和@After
        if (isBefore == null) {
            map = columnNameMap;
        } else {
            for (Map.Entry<String, Object> stringObjectEntry : columnNameMap.entrySet()) {
                String column = stringObjectEntry.getKey();
                Object[] values = (Object[]) stringObjectEntry.getValue();
                Object target = values[isBefore ? 0 : 1];
                map.put(column, target);
            }
        }
        return map;
    }

    private static MethodParamValue get(MethodParamKey methodParamKey, MethodParameter methodParameter) {
        MethodParamValue value;
        if ((value = MAP.get(methodParamKey)) != null) {
            return value;
        }
        value = new MethodParamValue();
        value.name = methodParameter.getName();
        // 方法形参注解又After,Before,Column,Column 别名的优先级最高
        for (Annotation annotation : methodParameter.getAnnotations()) {
            if (annotation instanceof Column) {
                Column column = (Column) annotation;
                value.hasColumnSetName = !"".equals(column.value());
                value.name = value.hasColumnSetName ? column.value() : methodParameter.getName();
            }

            if (annotation instanceof Before) {
                value.isBefore = Boolean.TRUE;
                Before before = (Before) annotation;
                if (!value.hasColumnSetName) {
                    value.name = "".equals(before.value()) ? methodParameter.getName() : before.value();
                }
            } else if (annotation instanceof After) {
                value.isBefore = Boolean.FALSE;
                After after = (After) annotation;
                if (!value.hasColumnSetName) {
                    value.name = "".equals(after.value()) ? methodParameter.getName() : after.value();
                }
            }

            // 如果注解有CurrentPosition修饰
            if (annotation instanceof CurrentPosition) {
                value.isCurrentPosition = true;
                // 如果注解有NextPosition修饰
            } else if (annotation instanceof NextPosition) {
                value.isNextPosition = true;
            }
        }
        MAP.putIfAbsent(methodParamKey, value);
        return MAP.get(methodParamKey);
    }

    private static Object getObject(Class parameterClazz, Object obj) {
        // 获取属性类型
        if (obj == null) {
            return null;
        }
        Class objClass = obj.getClass();
        // 需要将objClass转成parameterClazz
        if (parameterClazz != objClass) {
            ObjectTransform transform = TransformHolder.getTransform(parameterClazz);
            // obj可能是一个具体的类型
            if (transform == null) {
                return get(CLASS_FIELD_THREAD_LOCAL.get(), MAP_THREAD_LOCAL.get(), METHOD_PARAM_VALUE_THREAD_LOCAL.get().isBefore);
            }
            obj = transform.transform(obj);
            if (obj == null) {
                if (LOGGER.isWarnEnabled()) {
                    LOGGER.warn(String.format("[ %s ]类没有扩展于超类[ %s ]",
                            parameterClazz.getName(), ObjectTransform.class.getName()));
                }
            }
        }
        return obj;
    }

    /**
     * 通过columnName与对象绑定
     *
     * @param tableElements
     * @return
     */
    private static Map<String, Object> getStringObjectMap(List<TableElement> tableElements) {
        Map<String, Object> columnNameMap = new HashMap<>();
        for (TableElement tableElement : tableElements) {
            // 1开始
            Object obj = tableElement.getObj();
            String columnName = tableElement.getColumns().getColumnName();
            columnNameMap.put(columnName, obj);
        }
        return columnNameMap;
    }


    private static class MethodParamValue {
        /**
         * 是否通过@Column设置了别名
         */
        boolean hasColumnSetName = false;
        /**
         * 如果为null 没有@Before和@After 如果为true ,则用@Beofre ;false 则有@After
         * 如果@Before和@After都存在，只判断@Before
         */
        Boolean isBefore = null;
        /**
         * 纠正方法形参名与@Column或者(@Beofre或@After)的一致
         */
        String name;

        boolean isPrimate;

        boolean isCurrentPosition;
        boolean isNextPosition;
    }


    private static class MethodParamKey {
        MethodMetadata methodMetadata;
        int paramIndex;


        public MethodParamKey(MethodMetadata methodMetadata, int paramIndex) {
            this.methodMetadata = methodMetadata;
            this.paramIndex = paramIndex;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MethodParamKey that = (MethodParamKey) o;
            return paramIndex == that.paramIndex &&
                    Objects.equals(methodMetadata, that.methodMetadata);
        }

        @Override
        public int hashCode() {
            return Objects.hash(methodMetadata, paramIndex);
        }
    }
}
