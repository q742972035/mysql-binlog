package zy.opensource.mysql.binlog.incr.expose.event;

import zy.opensource.mysql.binlog.incr.expose.build.*;

/**
 * @program: mysql-binlog-incr-expose
 * @description
 * @author: zy
 * @create: 2019-08-09 11:23
 **/
public interface EventInfoMergeListener {
    /**
     * 接收到的是基本信息
     * @param eventInfoMerge
     */
    default void onEvent(BaseEventInfoMerge eventInfoMerge){}

    /**
     * 接收到的是mysiam等非事务关系的dml语句
     * @param commitDMLEventInfoMerge
     */
    default void onEvent(CommitDMLEventInfoMerge commitDMLEventInfoMerge){}

    /**
     * 接收到的是ddl语句
     * @param ddlEventInfoMerge
     */
    default void onEvent(DDLEventInfoMerge ddlEventInfoMerge){}

    /**
     * 接收到的是innodb等事务关系的dml语句
     * @param dmlEventInfoMerge
     */
    default void onEvent(DMLEventInfoMerge dmlEventInfoMerge){}

    /**
     * commit和xid都会调用在这个，也包含了数据表的信息扩展
     * @param xidDMLEventInfoMerge
     */
    default void onEvent(XidDMLEventInfoMerge xidDMLEventInfoMerge){}

}
