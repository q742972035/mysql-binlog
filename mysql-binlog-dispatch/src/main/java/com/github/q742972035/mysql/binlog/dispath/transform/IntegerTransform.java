package com.github.q742972035.mysql.binlog.dispath.transform;

public class IntegerTransform implements ObjectTransform<Integer> {
    @Override
    public Integer transform(Object obj) {
        return Integer.parseInt(obj.toString());
    }
}
