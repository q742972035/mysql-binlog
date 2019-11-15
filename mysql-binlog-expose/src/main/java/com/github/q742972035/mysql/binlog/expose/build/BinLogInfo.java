package com.github.q742972035.mysql.binlog.expose.build;

/**
 * @program: mysql-binlog-incr-expose
 * @description
 * @author: zy
 * @create: 2019-08-12 11:26
 **/
public class BinLogInfo {
    private final String binlogFileName;
    private final long binlogPosition;

    public BinLogInfo(String binlogFileName, long binlogPosition) {
        this.binlogFileName = binlogFileName;
        this.binlogPosition = binlogPosition;
    }

    public String getBinlogFileName() {
        return binlogFileName;
    }

    public long getBinlogPosition() {
        return binlogPosition;
    }
}
