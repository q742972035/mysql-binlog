package com.github.q742972035.mysql.binlog.expose.exception;

/**
 * @program: mysql-binlog-incr-expose
 * @description
 * @author: zy
 * @create: 2019-08-13 16:23
 **/
public class EventMergeException extends Exception {
    public EventMergeException(String msg) {
        super(msg);
    }
}
