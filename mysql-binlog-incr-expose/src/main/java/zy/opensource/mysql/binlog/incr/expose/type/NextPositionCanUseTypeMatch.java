package zy.opensource.mysql.binlog.incr.expose.type;

import zy.opensource.mysql.binlog.incr.expose.build.EventInfo;

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
