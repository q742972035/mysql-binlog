package com.github.q742972035.mysql.binlog.dispath.transform;


import java.sql.Date;

public class Date2Transform implements ObjectTransform<Date> {
    @Override
    public Date transform(Object obj) {
        if (obj instanceof Long) {
            return new Date((Long) obj);
        }
        return null;
    }
}
