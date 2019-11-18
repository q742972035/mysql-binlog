package com.github.q742972035.mysql.binlog.dispath.transform;

public interface ObjectTransform<T> {
    T transform(Object obj);
}
