package com.github.q742972035.mysql.binlog.dispath.list;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BodyTaskNameBind {
    /**
     * zy.opsource.dispath.list.TaskBody#bodykey->zy.opsource.dispath.list.ReadWriteLinkedListGroup#taskName 的绑定关系
     */
    static Map<String,String> BODY_TASKNAME_MAP = new ConcurrentHashMap<>();
}
