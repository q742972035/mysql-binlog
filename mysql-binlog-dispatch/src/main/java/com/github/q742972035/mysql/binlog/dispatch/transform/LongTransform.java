package com.github.q742972035.mysql.binlog.dispatch.transform;

public class LongTransform implements ObjectTransform<Long> {
    @Override
    public Long transform(Object obj) {
        return Long.parseLong(obj.toString());
    }
}
