package com.github.q742972035.mysql.binlog.dispath.transform;

import java.sql.Timestamp;
import java.util.Date;

public class Date1Transform implements ObjectTransform<Date> {
    @Override
    public Date transform(Object obj) {
        if (obj instanceof Long) {
            return new Date((Long) obj);
        }else if (obj instanceof Timestamp){
            return new Date(((Timestamp) obj).getTime());
        }
        return null;
    }
}
