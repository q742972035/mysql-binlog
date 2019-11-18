package com.github.q742972035.mysql.binlog.dispath.list;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 获取最小任务数策略
 */
public class SimpleFindLeastTaskStrategy<T> implements FindLeastTaskStrategy<T>, Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleFindLeastTaskStrategy.class);

    private Map<String, ReadWriteLinkedList<T>> taskMap;
    private ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);

    private volatile String leastTaskName;
    private volatile ReadWriteLinkedList<T> leastTask;

    public SimpleFindLeastTaskStrategy(Map<String, ReadWriteLinkedList<T>> taskMap) {
        this.taskMap = taskMap;
        executor.execute(this);
    }

    @Override
    public String findLeastTaskName() {
        return leastTaskName;
    }

    @Override
    public ReadWriteLinkedList<T> findLeastTask() {
        return leastTask;
    }

    @Override
    public void run() {
        try {
            // 排序
            TreeMap<String, ReadWriteLinkedList<T>> treeMap = new TreeMap<>((o1, o2) -> {
                try {
                    long l = taskMap.get(o1).remainTaskCount() - taskMap.get(o2).remainTaskCount();
                    return (int) l;
                } catch (Exception e) {
                    return 0;
                }
            });
            treeMap.putAll(SimpleFindLeastTaskStrategy.this.taskMap);
            for (Map.Entry<String, ReadWriteLinkedList<T>> entry : treeMap.entrySet()) {
                leastTaskName = entry.getKey();
                leastTask = entry.getValue();
                break;
            }
        } catch (Exception e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("", e);
            }
        } finally {
            executor.schedule(this, 1, TimeUnit.SECONDS);
        }
    }
}
