package com.github.q742972035.mysql.binlog.dispath.batch.handler;


import com.github.q742972035.mysql.binlog.expose.build.BaseEventInfoMerge;

public interface EventInfoMergeHandler<T extends BaseEventInfoMerge> {
    void handlerEventInfoMerge(T t);
}
