package com.github.q742972035.mysql.binlog.dispatch.list;

/**
 * 任务体
 *
 * @program: mysql-binlog-dispath
 * @description:
 * @author: 张忆
 * @create: 2019-10-12 00:48
 **/
public class SubTaskBody<T> {
    String subTaskName;
    T task;

    public SubTaskBody(String subTaskName) {
        this.subTaskName = subTaskName;
    }

    public SubTaskBody(String subTaskName, T task) {
        this.subTaskName = subTaskName;
        this.task = task;
    }

    public T getTask() {
        return task;
    }

    public SubTaskBody<T> setTask(T task) {
        this.task = task;
        return this;
    }

    public String getSubTaskName() {
        return subTaskName;
    }

    public synchronized String getTaskName() {
        return BodyTaskNameBind.BODY_TASKNAME_MAP.get(subTaskName);
    }

    public synchronized boolean setTaskName(String taskName, String oldTaskName) {
        String _OldTaskName = BodyTaskNameBind.BODY_TASKNAME_MAP.put(subTaskName, taskName);
        // 存在旧值,并且旧值与预先值不一致，设回旧的值
        if (_OldTaskName != null && !_OldTaskName.equals(oldTaskName)) {
            BodyTaskNameBind.BODY_TASKNAME_MAP.put(subTaskName, _OldTaskName);
            return false;
        }
        return true;
    }
}
