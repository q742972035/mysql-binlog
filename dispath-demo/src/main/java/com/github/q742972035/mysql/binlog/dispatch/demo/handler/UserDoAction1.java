package com.github.q742972035.mysql.binlog.dispatch.demo.handler;

import com.github.q742972035.mysql.binlog.dispatch.demo.dto.User;
import com.github.q742972035.mysql.binlog.dispatch.handler.BaseHandlerAction;

public class UserDoAction1 extends BaseHandlerAction<User> {

    private UserDao userDao;
    private UserStatisDao userStatisDao;

    public UserDoAction1(UserDao userDao,UserStatisDao userStatisDao) {
        this.userDao = userDao;
        this.userStatisDao = userStatisDao;
    }

    @Override
    public void doInsert(User user) {
        Long id = user.getId();
        if (id < maxId) {
            return;
        }
        userStatisDao.usercountAddOne();
    }

    @Override
    public void doUpdate(User before, User after) {

    }

    @Override
    public void doDelete(User user) {
        Long id = user.getId();
        if (id < maxId) {
            return;
        }
        userStatisDao.usercountDeleteOne();
    }

    private volatile long maxId;

    @Override
    public void init() {
        // 获取User目前的最大id
        maxId = userDao.getMaxId();
        // 往其他表初始化user数量
        userStatisDao.refreshAllUserCount();
    }
}
