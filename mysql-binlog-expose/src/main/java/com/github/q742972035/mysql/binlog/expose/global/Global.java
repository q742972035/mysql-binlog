package com.github.q742972035.mysql.binlog.expose.global;

/**
 * 定义一个全局属性
 * @program: expose
 * @description:
 * @author: 张忆
 * @create: 2019-11-17 07:55
 **/
public class Global {
    /**
     * 定义DefaultBinaryLogEventListener轮到的当前DB（shcema）
     */
    public static final ThreadLocal<String> CURRENT_DB = new ThreadLocal<>();
    /**
     * 定义DefaultBinaryLogEventListener轮到的当前DB(table)
     */
    public static final ThreadLocal<String> CURRENT_TB = new ThreadLocal<>();

    public static final ThreadLocal<Long> CURRENT_POSITION = new ThreadLocal<>();

    public static final ThreadLocal<Long> NEXT_POSITION = new ThreadLocal<>();
}
