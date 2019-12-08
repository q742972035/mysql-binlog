package com.github.q742972035.mysql.binlog.dispatch.scan.tablehandler;

import com.github.q742972035.mysql.binlog.dispatch.annotation.TableHandler;
import com.github.q742972035.mysql.binlog.dispatch.exception.GenericClassNotFoundException;
import com.github.q742972035.mysql.binlog.dispatch.handler.BaseTableHandler;
import com.github.q742972035.mysql.binlog.dispatch.scan.dto.TestBinlogInno;

import java.util.Arrays;


@TableHandler(tableName = "test_binlog_inno")
public class TestBinlogInnoHandler extends BaseTableHandler<TestBinlogInno> {

    public TestBinlogInnoHandler() throws GenericClassNotFoundException {
        super();
        setHandlerActions(Arrays.asList(new TestBinlogInnoDailyAction(),new TestBinlogInnoDailyAction1()));
    }
}
