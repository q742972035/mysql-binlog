package com.github.q742972035.mysql.binlog.expose.build;


import com.alibaba.druid.pool.DruidDataSource;
import com.github.q742972035.mysql.binlog.expose.build.strategy.ErrorCodeStrategy;
import com.github.q742972035.mysql.binlog.expose.build.strategy.WarnErrorCodeStragegy;
import com.github.q742972035.mysql.binlog.expose.event.ConnectionEventListener;
import com.github.q742972035.mysql.binlog.expose.event.EventInfoMergeListener;
import com.github.q742972035.mysql.binlog.expose.event.FailureEventListener;
import com.github.q742972035.mysql.binlog.expose.filter.EventInfoFilter;
import com.github.q742972035.mysql.binlog.expose.filter.SchemaEventInfoFilter;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ExposeConfig {


    /**
     * 添加eventInfo的过滤器
     */
    private List<EventInfoFilter> eventInfoFilters = new ArrayList<>();
    {
        // 过滤schema
        eventInfoFilters.add(new SchemaEventInfoFilter());
    }


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

    public List<EventInfoFilter> getEventInfoFilter() {
        return eventInfoFilters;
    }

    public ExposeConfig setEventInfoFilter(EventInfoFilter eventInfoFilter) {
        this.eventInfoFilters.add(eventInfoFilter);
        return this;
    }

    public ExposeConfig setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        return this;
    }

    public DataSource getDataSource() {
        if (dataSource==null){
            dataSource = buildDefaultDataSource();
        }
        return dataSource;
    }

    public void setErrorCodeStrategy(ErrorCodeStrategy errorCodeStrategy) {
        this.errorCodeStrategy = errorCodeStrategy;
    }

    public ErrorCodeStrategy getErrorCodeStrategy() {
        return errorCodeStrategy;
    }


    private String defaultUrl;
    private int defaultPort =3306;
    private String defaultUserName;
    private String defaultPassword;
    private String schema;


    public String getSchema() {
        return schema;
    }

    private DataSource buildDefaultDataSource(){
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setDriverClassName("com.mysql.jdbc.Driver");
        druidDataSource.setUrl(String.format("jdbc:mysql://%s:%s/?useUnicode=true&characterEncoding=UTF8&serverTimezone=Asia/Shanghai",defaultUrl, defaultPort));
        druidDataSource.setUsername(defaultUserName);
        druidDataSource.setPassword(defaultPassword);
        return druidDataSource;
    }

}
