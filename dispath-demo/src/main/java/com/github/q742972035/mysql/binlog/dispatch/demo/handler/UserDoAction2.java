package com.github.q742972035.mysql.binlog.dispatch.demo.handler;

import com.github.q742972035.mysql.binlog.dispatch.demo.dto.User;
import com.github.q742972035.mysql.binlog.dispatch.handler.BaseHandlerAction;

public class UserDoAction2 extends BaseHandlerAction<User> {


    @Override
    public void doInsert(User user) {

    }

    @Override
    public void doUpdate(User before, User after) {

    }

    @Override
    public void doDelete(User user) {

    }

    @Override
    public void init() {

    }
}
