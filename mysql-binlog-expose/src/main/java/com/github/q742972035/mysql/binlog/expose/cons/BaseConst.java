package com.github.q742972035.mysql.binlog.expose.cons;

/**
 * @program: mysql-binlog-incr-expose
 * @description:
 * @author: 张忆
 * @create: 2019-08-03 00:38
 **/
public interface BaseConst {
    String CREATE_LOW = "CREATE".toLowerCase();
    int CREATE_LEN = CREATE_LOW.length();

    String ALTER_LOW = "ALTER".toLowerCase();
    int ALTER_LEN = ALTER_LOW.length();

    String DROP_LOW = "DROP".toLowerCase();
    int DROP_LEN = DROP_LOW.length();

    String TRUNCATE_LOW = "TRUNCATE".toLowerCase();
    int TRUNCATE_LEN = TRUNCATE_LOW.length();

    String BEGIN = "BEGIN";
    String COMMIT = "COMMIT";
}
