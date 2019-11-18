package com.github.q742972035.mysql.binlog.dispath.transform;

public class BooleanTransform implements ObjectTransform<Boolean> {
    @Override
    public Boolean transform(Object obj) {
        return Boolean.parseBoolean(obj.toString());
    }
}
