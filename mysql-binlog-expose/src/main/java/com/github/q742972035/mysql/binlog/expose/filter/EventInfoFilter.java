package com.github.q742972035.mysql.binlog.expose.filter;

import com.github.q742972035.mysql.binlog.expose.build.ExposeConfig;
import com.github.q742972035.mysql.binlog.expose.build.EventInfo;
import com.github.q742972035.mysql.binlog.expose.build.ExposeConfig;

/**
 * @program: mysql-binlog-incr-expose
 * @description
 * @author: zy
 * @create: 2019-08-09 11:19
 **/
public interface EventInfoFilter {
    boolean filter(ExposeConfig config, EventInfo eventInfo);
}
