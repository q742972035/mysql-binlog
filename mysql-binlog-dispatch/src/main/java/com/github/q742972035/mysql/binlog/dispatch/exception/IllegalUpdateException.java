package com.github.q742972035.mysql.binlog.dispatch.exception;

/**
 * @program: mysql-binlog-dispath
 * @description:
 * @author: 张忆
 * @create: 2019-11-03 23:25
 **/
public class IllegalUpdateException extends RuntimeException{

    public IllegalUpdateException(String msg) {
        super(msg);
    }
}
