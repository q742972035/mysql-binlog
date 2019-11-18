package com.github.q742972035.mysql.binlog.dispath.exception;

/**
 * @program: mysql-binlog-dispath
 * @description:
 * @author: 张忆
 * @create: 2019-11-17 23:16
 **/
public class TableHandlerExistException extends RuntimeException{
    public TableHandlerExistException(String message) {
        super(message);
    }
}
