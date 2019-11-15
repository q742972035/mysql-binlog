package com.github.q742972035.mysql.binlog.expose.common;

/**
 * 字符串拼接器
 * @program: mysql-binlog-incr-expose
 * @description
 * @author: zy
 * @create: 2019-08-22 09:02
 **/
public class Stringjointer {
    private StringBuilder stringBuilder = new StringBuilder();

    private final String jointOperator ;

    public Stringjointer(String jointOperator) {
        this.jointOperator = jointOperator;
    }

    public void append(String str){
        if (stringBuilder.length()>0){
            stringBuilder.append(jointOperator);
        }
        stringBuilder.append(str);
    }

    public String get(){
        return stringBuilder.toString();
    }
}
