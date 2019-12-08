package com.github.q742972035.mysql.binlog.dispatch.transform;

public class ByteTransform implements ObjectTransform<Byte> {
    @Override
    public Byte transform(Object obj) {
        return Byte.parseByte(obj.toString());
    }
}
