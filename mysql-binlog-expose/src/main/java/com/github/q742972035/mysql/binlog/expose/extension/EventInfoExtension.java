package com.github.q742972035.mysql.binlog.expose.extension;

import com.github.q742972035.mysql.binlog.expose.build.EventInfo;
import com.github.q742972035.mysql.binlog.expose.setter.ObjectWrapper;

/**
 * 增对eventInfo的扩展
 *
 * @program: mysql-binlog-incr-expose
 * @description
 * @author: zy
 * @create: 2019-08-08 09:45
 **/
public interface EventInfoExtension extends ObjectWrapper<EventInfo> {
    /**
     * 总步骤
     *
     * @return
     */
    int getStepCount();

    /**
     * 当前步骤，从1开始，最大值是stepCount();
     *
     * @return
     */
    int getCurrentStep();

    /**
     * 是否最后一个步骤
     *
     * @return
     */
    boolean isLastStep();

    /**
     * 是否第一个步骤
     *
     * @return
     */
    boolean isFirstStep();

    EventInfo getEventInfo();

    /**
     * 是否有用
     * @return
     */
    boolean isUseful();

}
