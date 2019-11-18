package com.github.q742972035.mysql.binlog.dispath.transform;

public class LongTransform implements ObjectTransform<Long> {
    @Override
    public Long transform(Object obj) {
        return Long.parseLong(obj.toString());
    }
}
