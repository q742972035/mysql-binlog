package com.github.q742972035.mysql.binlog.expose.setter;

/**
 * @program: mysql-binlog-incr-expose
 * @description
 * @author: zy
 * @create: 2019-08-08 09:45
 **/
public interface ObjectWrapper<T> {
    void setWrap(T obj);
}
