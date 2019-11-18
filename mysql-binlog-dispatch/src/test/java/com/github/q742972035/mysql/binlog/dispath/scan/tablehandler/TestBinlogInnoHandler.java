package com.github.q742972035.mysql.binlog.dispath.scan.tablehandler;

import com.github.q742972035.mysql.binlog.dispath.annotation.TableHandler;
import com.github.q742972035.mysql.binlog.dispath.exception.GenericClassNotFoundException;
import com.github.q742972035.mysql.binlog.dispath.handler.BaseTableHandler;
import com.github.q742972035.mysql.binlog.dispath.scan.dto.TestBinlogInno;

import java.util.Arrays;


@TableHandler(tableName = "test_binlog_inno")
public class TestBinlogInnoHandler extends BaseTableHandler<TestBinlogInno> {

    public TestBinlogInnoHandler() throws GenericClassNotFoundException {
        super();
        setHandlerActions(Arrays.asList(new TestBinlogInnoDailyAction(),new TestBinlogInnoDailyAction1()));
    }
}
