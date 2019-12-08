package com.github.q742972035.mysql.binlog.dispatch.scan.filter;

public interface ClassLoaderContextFilter {
    boolean beforeCreate(String className);
}
