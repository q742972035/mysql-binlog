package com.github.q742972035.mysql.binlog.expose.build.mysql.sql;

/**
 * 通过sql的%s格式和占位符的字符串，获取sql语句
 * @program: mysql-binlog-incr-expose
 * @description
 * @author: zy
 * @create: 2019-08-19 15:57
 **/
public class SqlAcquire {
    public static String getSql(String sqlFormat,String...strs){
        return String.format(sqlFormat,strs);
    }
}
