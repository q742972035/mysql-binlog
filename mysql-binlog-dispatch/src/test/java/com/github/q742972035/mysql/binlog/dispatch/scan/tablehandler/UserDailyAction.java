package com.github.q742972035.mysql.binlog.dispatch.scan.tablehandler;

import com.github.q742972035.mysql.binlog.dispatch.handler.BaseHandlerAction;
import com.github.q742972035.mysql.binlog.dispatch.scan.dto.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 用户每日统计
 */
public class UserDailyAction extends BaseHandlerAction<User> {
    private Logger logger = LoggerFactory.getLogger(getClass());


    @Override
    public void init() {

    }

    @Override
    public void doInsert(User user) {
        logger.info(user.toString());
    }

    @Override
    public void doUpdate(User before, User after) {

    }

    @Override
    public void doDelete(User user) {
    }
}
