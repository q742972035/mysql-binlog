package com.github.q742972035.mysql.binlog.dispath.utils;

/**
 * @program: mysql-binlog-dispath
 * @description:
 * @author: 张忆
 * @create: 2019-10-02 14:33
 **/
public class PrintUtils {
    public static String print(String... strings) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < strings.length; i++) {
            builder.append(strings[i]);
            if (i < strings.length - 1) {
                builder.append(",");
            }
        }
        return builder.toString();
    }
}
