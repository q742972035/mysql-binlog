package zy.opensource.mysql.binlog.incr.expose.build;


import com.alibaba.druid.pool.DruidDataSource;
import zy.opensource.mysql.binlog.incr.expose.build.strategy.ErrorCodeStrategy;
import zy.opensource.mysql.binlog.incr.expose.build.strategy.WarnErrorCodeStragegy;
import zy.opensource.mysql.binlog.incr.expose.event.ConnectionEventListener;
import zy.opensource.mysql.binlog.incr.expose.event.EventInfoMergeListener;
import zy.opensource.mysql.binlog.incr.expose.event.FailureEventListener;
import zy.opensource.mysql.binlog.incr.expose.filter.EventInfoFilter;

import javax.sql.DataSource;
import java.util.concurrent.CopyOnWriteArrayList;

public class ExposeConfig {


    /**
     * 添加eventInfo的过滤器
     */
    private EventInfoFilter eventInfoFilter;

    private DataSource dataSource;

    /**
     * 错误码非0策略
     */
    private ErrorCodeStrategy errorCodeStrategy = new WarnErrorCodeStragegy();



    /**
     * 监听器
     */
    CopyOnWriteArrayList<EventInfoMergeListener> eventInfoMergeListeners = new CopyOnWriteArrayList<>();

    /**
     * 连接事件
     */
    CopyOnWriteArrayList<ConnectionEventListener> connectionEvents = new CopyOnWriteArrayList<>();

    /**
     * 失败事件
     */
    CopyOnWriteArrayList<FailureEventListener> failureEvents = new CopyOnWriteArrayList<>();

    public CopyOnWriteArrayList<FailureEventListener> getFailureEvents() {
        return failureEvents;
    }

    public ExposeConfig addFailureEvent(FailureEventListener failureEvent) {
        failureEvents.add(failureEvent);
        return this;
    }

    public ExposeConfig removeFailureEvent(FailureEventListener failureEvent) {
        for (FailureEventListener event : failureEvents) {
            if (event == failureEvent) {
                failureEvents.remove(event);
            }
        }
        return this;
    }


    public CopyOnWriteArrayList<ConnectionEventListener> getConnectionEvents() {
        return connectionEvents;
    }

    public ExposeConfig addConnectionEvent(ConnectionEventListener connectionEvent) {
        connectionEvents.add(connectionEvent);
        return this;
    }

    public ExposeConfig removeConnectionEvent(ConnectionEventListener connectionEvent) {
        for (ConnectionEventListener event : connectionEvents) {
            if (event == connectionEvent) {
                connectionEvents.remove(event);
            }
        }
        return this;
    }

    public CopyOnWriteArrayList<EventInfoMergeListener> getEventInfoMergeListeners() {
        return eventInfoMergeListeners;
    }

    public ExposeConfig addEventInfoMergeListener(EventInfoMergeListener eventInfoMergeListener) {
        this.eventInfoMergeListeners.add(eventInfoMergeListener);
        return this;
    }

    public ExposeConfig removeEventInfoMergeListener(EventInfoMergeListener eventInfoMergeListener) {
        for (EventInfoMergeListener infoMergeListener : eventInfoMergeListeners) {
            if (infoMergeListener == eventInfoMergeListener) {
                eventInfoMergeListeners.remove(infoMergeListener);
            }
        }
        return this;
    }

    public ExposeConfig setEventInfoFilter(EventInfoFilter eventInfoFilter) {
        this.eventInfoFilter = eventInfoFilter;
        return this;
    }

    public EventInfoFilter getEventInfoFilter() {
        return eventInfoFilter;
    }

    public ExposeConfig setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        return this;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setErrorCodeStrategy(ErrorCodeStrategy errorCodeStrategy) {
        this.errorCodeStrategy = errorCodeStrategy;
    }

    public ErrorCodeStrategy getErrorCodeStrategy() {
        return errorCodeStrategy;
    }
}
