package com.github.q742972035.mysql.binlog.dispath.list;

/**
 * 获取最小任务数策略
 */
public interface FindLeastTaskStrategy<T> {
    String findLeastTaskName();
    ReadWriteLinkedList<T> findLeastTask();
}
