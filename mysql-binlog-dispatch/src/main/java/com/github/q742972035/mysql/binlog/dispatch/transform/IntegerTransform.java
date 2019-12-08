package com.github.q742972035.mysql.binlog.dispatch.transform;

public class IntegerTransform implements ObjectTransform<Integer> {
    @Override
    public Integer transform(Object obj) {
        return Integer.parseInt(obj.toString());
    }
}
