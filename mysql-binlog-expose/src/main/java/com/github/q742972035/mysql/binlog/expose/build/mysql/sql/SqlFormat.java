package com.github.q742972035.mysql.binlog.expose.build.mysql.sql;

/**
 * @program: mysql-binlog-incr-expose
 * @description
 * @author: zy
 * @create: 2019-08-19 15:56
 **/
public interface SqlFormat {

    String COLUMN_SQL = "SELECT COLUMN_NAME,COLUMN_DEFAULT,IS_NULLABLE,DATA_TYPE,COLUMN_TYPE,COLUMN_KEY FROM information_schema.`COLUMNS` WHERE TABLE_SCHEMA = '%s' AND TABLE_NAME = '%s' ORDER BY ORDINAL_POSITION ASC";


}
