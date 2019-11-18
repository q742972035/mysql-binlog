package com.github.q742972035.mysql.binlog.dispath.transform;

import java.util.HashMap;
import java.util.Map;

/**
 * 转换支持
 */
public class TransformHolder {

    private static final Map<Class, ObjectTransform> MAP = new HashMap<>();

    static {
        MAP.put(Boolean.class, new BooleanTransform());
        MAP.put(Boolean.TYPE, new BooleanTransform());
        MAP.put(Byte.class, new ByteTransform());
        MAP.put(Byte.TYPE, new ByteTransform());
        MAP.put(Character.class, new CharacterTransform());
        MAP.put(Character.TYPE, new CharacterTransform());
        MAP.put(Double.class, new DoubleTransform());
        MAP.put(Double.TYPE, new DoubleTransform());
        MAP.put(Float.class, new FloatTransform());
        MAP.put(Float.TYPE, new FloatTransform());
        MAP.put(Integer.class, new IntegerTransform());
        MAP.put(Integer.TYPE, new IntegerTransform());
        MAP.put(Long.class, new LongTransform());
        MAP.put(Long.TYPE, new LongTransform());
        MAP.put(Short.class, new ShortTransform());
        MAP.put(Short.TYPE, new ShortTransform());

        MAP.put(String.class, new StringTransform());
        MAP.put(java.util.Date.class, new Date1Transform());
        MAP.put(java.sql.Date.class, new Date2Transform());


    }

    /**
     * 自定义一些新的类型
     */
    public static synchronized void registerObjectTransform(Class key, ObjectTransform value) {
        MAP.put(key, value);
    }

    public static ObjectTransform getTransform(Class clazz) {
        return MAP.get(clazz);
    }
}
