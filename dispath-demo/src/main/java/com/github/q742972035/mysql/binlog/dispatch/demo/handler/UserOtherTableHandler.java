package com.github.q742972035.mysql.binlog.dispatch.demo.handler;

import com.github.q742972035.mysql.binlog.dispatch.annotation.TableHandler;
import com.github.q742972035.mysql.binlog.dispatch.demo.dto.User;
import com.github.q742972035.mysql.binlog.dispatch.exception.GenericClassNotFoundException;
import com.github.q742972035.mysql.binlog.dispatch.handler.BaseTableHandler;

import java.util.Arrays;


@TableHandler(tableName = "user")
public class UserOtherTableHandler extends BaseTableHandler<User> {

    public UserOtherTableHandler() throws GenericClassNotFoundException {
        setHandlerActions(Arrays.asList(
                // user得操作1
                new UserDoAction1()
                // user得操作2
                ,new UserDoAction2()
        ));
    }
}
