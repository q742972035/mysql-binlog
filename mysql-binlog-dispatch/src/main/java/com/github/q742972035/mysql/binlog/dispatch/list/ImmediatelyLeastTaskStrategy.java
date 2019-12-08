package com.github.q742972035.mysql.binlog.dispatch.list;

import java.util.HashMap;
import java.util.Map;

public class ImmediatelyLeastTaskStrategy<T> implements FindLeastTaskStrategy<T> {

    private Map<String, ReadWriteLinkedList<T>> taskMap;


    public ImmediatelyLeastTaskStrategy(Map<String, ReadWriteLinkedList<T>> taskMap) {
        this.taskMap = taskMap;
    }

    @Override
    public String findLeastTaskName() {
        long minTaskCount = Long.MAX_VALUE;
        String minTaskName = null;
        Map<String, ReadWriteLinkedList<T>> map = new HashMap<>(taskMap);
        for (Map.Entry<String, ReadWriteLinkedList<T>> entry : map.entrySet()) {
            String taskName = entry.getKey();
            long remainTaskCount = entry.getValue().remainTaskCount();
            if (remainTaskCount < minTaskCount) {
                minTaskCount = remainTaskCount;
                minTaskName = taskName;
            }
        }
        return minTaskName;
    }

    @Override
    public ReadWriteLinkedList<T> findLeastTask() {
        long minTaskCount = Long.MAX_VALUE;
        ReadWriteLinkedList<T> minTask = null;
        Map<String, ReadWriteLinkedList<T>> map = new HashMap<>(taskMap);
        for (Map.Entry<String, ReadWriteLinkedList<T>> entry : map.entrySet()) {
            String taskName = entry.getKey();
            long remainTaskCount = entry.getValue().remainTaskCount();
            if (remainTaskCount < minTaskCount) {
                minTaskCount = remainTaskCount;
                minTask = entry.getValue();
            }
        }
        return minTask;
    }
}
