package com.github.q742972035.mysql.binlog.expose.type;

import com.github.q742972035.mysql.binlog.expose.build.EventInfo;
import com.github.q742972035.mysql.binlog.expose.build.EventInfo;

/**
 * 判断nextPosition是否能够使用的匹配规则
 * @program: mysql-binlog-incr-expose
 * @description
 * @author: zy
 * @create: 2019-08-02 17:19
 **/
public interface NextPositionCanUseTypeMatch {
    boolean match(EventInfo eventInfo);
}
