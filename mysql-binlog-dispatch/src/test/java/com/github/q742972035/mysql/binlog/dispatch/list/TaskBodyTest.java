package com.github.q742972035.mysql.binlog.dispatch.list;

import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

public class TaskBodyTest {


    @Test
    public void testTaskNameSet(){
        SubTaskBody taskBody = new SubTaskBody("aaaa");
        String oldTaskName = taskBody.getTaskName();
        assertThat(taskBody.setTaskName("111",oldTaskName)).isTrue();
        oldTaskName = taskBody.getTaskName();
        assertThat(taskBody.setTaskName("111",oldTaskName+"aaa")).isFalse();
        assertThat(taskBody.setTaskName("222",oldTaskName)).isTrue();
    }
}