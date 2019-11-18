package com.github.q742972035.mysql.binlog.dispath.scan.tablehandler;

import com.github.q742972035.mysql.binlog.dispath.handler.BaseHandlerAction;
import com.github.q742972035.mysql.binlog.dispath.scan.dto.TestBinlogInno;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestBinlogInnoDailyAction extends BaseHandlerAction<TestBinlogInno> {
    private Logger logger = LoggerFactory.getLogger(getClass());


    @Override
    public void init() {

    }

    @Override
    public void doInsert(TestBinlogInno user) {
        logger.info("doInsert:"+user.getId());
//        logger.info("cp:{},np:{}",Global.);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void doUpdate(TestBinlogInno before, TestBinlogInno after) {
        logger.info("更新前:{},更新后:{}",before.toString(),after.toString());
    }

    @Override
    public void doDelete(TestBinlogInno user) {
        logger.info("doDelete:"+user.toString());
    }
}
