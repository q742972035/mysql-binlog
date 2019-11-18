package com.github.q742972035.mysql.binlog.dispath.transform;

public class ByteTransform implements ObjectTransform<Byte> {
    @Override
    public Byte transform(Object obj) {
        return Byte.parseByte(obj.toString());
    }
}
