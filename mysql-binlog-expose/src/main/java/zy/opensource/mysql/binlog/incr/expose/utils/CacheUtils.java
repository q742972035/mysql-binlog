package zy.opensource.mysql.binlog.incr.expose.utils;

import zy.opensource.mysql.binlog.incr.expose.map.ManagedConcurrentWeakHashMap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

/**
 * 代理执行方法，并且将结果缓存
 *
 * @program: mysql-binlog-incr-expose
 * @description
 * @author: zy
 * @create: 2019-08-20 19:57
 **/
public class CacheUtils {


    private static final Map<Info, Object> CACHE = new ManagedConcurrentWeakHashMap<>(256);


    public static Object cache(Info info) throws InvocationTargetException, IllegalAccessException {
        Object result;
        if ((result = CACHE.get(info)) != null) {
            return result;
        }

        ReflectionUtils.makeAccessible(info.method);
        result = info.method.invoke(info.object, info.args);
        CACHE.put(info,result);
        return result;
    }

    public static void clear(Info info){
        CACHE.remove(info);
    }


    public static class Info {
        Object object;
        Method method;
        Object[] args;

        public Info(Object object, Method method, Object...args) {
            this.object = object;
            this.method = method;
            this.args = args;
        }

        public Info(Method method, Object... args) {
            this.method = method;
            this.args = args;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Info info = (Info) o;
            return Objects.equals(method, info.method) &&
                    Arrays.equals(args, info.args);
        }

        @Override
        public int hashCode() {
            int result = Objects.hash(method);
            result = 31 * result + Arrays.hashCode(args);
            return result;
        }
    }
}
