package com.github.q742972035.mysql.binlog.expose.exception;

/**
 * @program: mysql-binlog-incr-expose
 * @description
 * @author: zy
 * @create: 2019-08-20 20:10
 **/
public class CacheUseException extends RuntimeException{
    public CacheUseException(Exception e){
        super(e);
    }
}
