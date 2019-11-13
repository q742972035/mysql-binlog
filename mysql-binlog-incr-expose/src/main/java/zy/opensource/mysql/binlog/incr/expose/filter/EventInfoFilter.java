package zy.opensource.mysql.binlog.incr.expose.filter;

import zy.opensource.mysql.binlog.incr.expose.build.EventInfo;
import zy.opensource.mysql.binlog.incr.expose.build.ExposeConfig;

/**
 * @program: mysql-binlog-incr-expose
 * @description
 * @author: zy
 * @create: 2019-08-09 11:19
 **/
public interface EventInfoFilter {
    boolean filter(ExposeConfig config,EventInfo eventInfo);
}
