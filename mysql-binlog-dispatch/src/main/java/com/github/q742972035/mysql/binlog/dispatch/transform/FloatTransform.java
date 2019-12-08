package com.github.q742972035.mysql.binlog.dispatch.transform;

public class FloatTransform implements ObjectTransform<Float> {
    @Override
    public Float transform(Object obj) {
        return Float.parseFloat(obj.toString());
    }
}
