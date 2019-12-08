package com.github.q742972035.mysql.binlog.dispatch.transform;

public class BooleanTransform implements ObjectTransform<Boolean> {
    @Override
    public Boolean transform(Object obj) {
        return Boolean.parseBoolean(obj.toString());
    }
}
