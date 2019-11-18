package com.github.q742972035.mysql.binlog.dispath.transform;

public class ShortTransform implements ObjectTransform<Short> {
    @Override
    public Short transform(Object obj) {
        return Short.parseShort(obj.toString());
    }
}
