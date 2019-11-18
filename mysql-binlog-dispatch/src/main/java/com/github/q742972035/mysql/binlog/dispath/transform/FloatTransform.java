package com.github.q742972035.mysql.binlog.dispath.transform;

public class FloatTransform implements ObjectTransform<Float> {
    @Override
    public Float transform(Object obj) {
        return Float.parseFloat(obj.toString());
    }
}
