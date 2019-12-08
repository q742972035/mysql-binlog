package com.github.q742972035.mysql.binlog.dispatch.transform;

public class DoubleTransform implements ObjectTransform<Double> {
    @Override
    public Double transform(Object obj) {
        return Double.parseDouble(obj.toString());
    }
}
