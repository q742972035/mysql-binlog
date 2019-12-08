package com.github.q742972035.mysql.binlog.dispatch.list;


import com.github.q742972035.mysql.binlog.dispatch.list.exception.NoSuchSubTaskException;
import com.github.q742972035.mysql.binlog.dispatch.list.exception.PrefixRepetitionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicLong;

/**
 * ReadWriteLinkedList的组合，
 * 插入任务T-A时候，如果当前有队列Q-A拥有任务T-A，则将任务插入到T-A
 * 否则，寻找队列任务数量最少的队列T-N，并将任务插入到T-N中
 **/
public class ReadWriteLinkedListGroup<T> {
    private static final Object EMPTY = new Object();
    private static final Map<String, Object> GLOBAL_PREFIX = new ConcurrentHashMap<>();
    /**
     * 定义组大小
     */
    private int groupSize;


    /**
     * 执行任务列表
     */
    private Map<String, SubTask<T>> taskMap;

    private Map<String, ReadWriteLinkedList.RunBack<SubTaskBody<T>>> runBackMap;

    /**
     * 一个taskName对应一个SubTask实例，实例里面包含多个subTaskName
     */
    private List<String> taskNames;
    private ThreadPoolExecutor executor;
    FindLeastTaskStrategy findLeastTaskCountStrategy;

    public ReadWriteLinkedListGroup(int groupSize, String prefix) throws PrefixRepetitionException {
        if (!prefix.endsWith("-")) {
            prefix = prefix + "-";
        }
        if (GLOBAL_PREFIX.putIfAbsent(prefix, EMPTY) != null) {
            throw new PrefixRepetitionException();
        }
        this.groupSize = groupSize;
        taskMap = new ConcurrentHashMap<>(groupSize);
        runBackMap = new ConcurrentHashMap<>(groupSize);
        taskNames = new ArrayList<>(groupSize);
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(groupSize);
        for (int i = 0; i < groupSize; i++) {
            addTaskName(prefix + i);
        }
        setRunBack();
        findLeastTaskCountStrategy = new ImmediatelyLeastTaskStrategy(taskMap);
    }

    /**
     * 添加任务
     * 如果taskNameSize>groupSize 不允许添加
     */
    private boolean addTaskName(String taskName) {
        this.taskMap.putIfAbsent(taskName, new SubTask<>());
        return taskNames.add(taskName);
    }

    public void excuseTask(SubTaskBody<T> taskBody) throws NoSuchSubTaskException {
        // 获取任务名字
        String taskName = taskBody.getTaskName();
        SubTask<T> task = getSubTask(taskBody, taskName);
        if (task == null) {
            throw new NoSuchSubTaskException();
        }
        task.addAndlooping(taskBody, executor);
    }


    public void setRunBack(String taskName, ReadWriteLinkedList.RunBack<SubTaskBody<T>> runBack) {
        runBackMap.putIfAbsent(taskName, runBack);
    }

    private void setRunBack() {
        for (Map.Entry<String, SubTask<T>> entry : taskMap.entrySet()) {
            String taskName = entry.getKey();
            entry.getValue().setRunBack(subTaskBody -> {
                ReadWriteLinkedList.RunBack<SubTaskBody<T>> subTaskBodyRunBack = runBackMap.get(taskName);
                if (subTaskBodyRunBack != null) {
                    subTaskBodyRunBack.result(subTaskBody);
                }
            });
        }
    }

    private SubTask<T> getSubTask(SubTaskBody<T> taskBody, String taskName) {
        SubTask<T> task = null;
        if (taskName == null) {
            taskName = resetTaskName(taskBody, taskName);
            task = this.taskMap.get(taskName);
        } else {
            task = this.taskMap.get(taskName);
            // 查询子任务剩余次数
            long subTaskRemain = task.remainTaskCount(taskBody.getSubTaskName());
            if (subTaskRemain == 0L) {
                taskName = resetTaskName(taskBody, taskName);
                task = this.taskMap.get(taskName);
            }
        }
        return task;
    }

    private String resetTaskName(SubTaskBody<T> taskBody, String oldTaskName) {
        // 查找队列长度最小的任务名字
        String listMinTaskName = null;
        while (listMinTaskName == null) {
            listMinTaskName = findLeastTaskCountStrategy.findLeastTaskName();
        }
        while (true) {
            if (taskBody.setTaskName(listMinTaskName, oldTaskName)) {
                return listMinTaskName;
            }
        }
    }


    public List<String> getAllTaskName() {
        return taskNames;
    }

    public int size() {
        return taskNames.size();
    }

    static class SubTask<T> extends ReadWriteLinkedList<SubTaskBody<T>> {
        private static final Logger LOGGER = LoggerFactory.getLogger(SubTask.class);
        /**
         * 记录子任务的剩余数量
         */
        Map<String, AtomicLong> subTaskRemain = new ConcurrentHashMap<>();


        @Override
        public void setRunBack(RunBack<SubTaskBody<T>> runBack) {
            super.setRunBack(subTaskBody -> {
                try {
                    runBack.result(subTaskBody);
                } catch (Exception e) {
                    if (LOGGER.isErrorEnabled()) {
                        LOGGER.error("", e);
                    }
                } finally {
                    this.subTaskRemain.get(subTaskBody.getSubTaskName()).decrementAndGet();
                }
            });
        }

        @Override
        public final void addAndlooping(SubTaskBody<T> subTaskBody, ThreadPoolExecutor executor) {
            synchronized (this) {
                super.addAndlooping(subTaskBody, executor);
            }
        }


        @Override
        public final void add(SubTaskBody<T> subTaskBody) {
            try {
                super.add(subTaskBody);
            } finally {
                doInCreatement(subTaskBody);
            }
        }

        private void doInCreatement(SubTaskBody<T> subTaskBody) {
            String subTaskName = subTaskBody.getSubTaskName();
            AtomicLong subTaskRemain = this.subTaskRemain.get(subTaskName);
            if (subTaskRemain == null) {
                this.subTaskRemain.putIfAbsent(subTaskName, new AtomicLong());
                subTaskRemain = this.subTaskRemain.get(subTaskName);
            }
            subTaskRemain.incrementAndGet();
        }

        public long remainTaskCount(String subTaskName) {
            return subTaskRemain.get(subTaskName).get();
        }
    }
}
