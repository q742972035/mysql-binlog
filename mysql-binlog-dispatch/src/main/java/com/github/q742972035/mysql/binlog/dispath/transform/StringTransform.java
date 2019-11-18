package com.github.q742972035.mysql.binlog.dispath.transform;

public class StringTransform implements ObjectTransform<String> {
    @Override
    public String transform(Object obj) {
        return obj.toString();
    }
}
