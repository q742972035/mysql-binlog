package com.github.q742972035.mysql.binlog.dispatch.demo.handler;

import com.github.q742972035.mysql.binlog.dispatch.annotation.Column;
import com.github.q742972035.mysql.binlog.dispatch.annotation.CurrentPosition;
import com.github.q742972035.mysql.binlog.dispatch.annotation.NextPosition;
import com.github.q742972035.mysql.binlog.dispatch.annotation.TableHandler;
import com.github.q742972035.mysql.binlog.dispatch.annotation.dml.After;
import com.github.q742972035.mysql.binlog.dispatch.annotation.dml.Before;
import com.github.q742972035.mysql.binlog.dispatch.annotation.dml.Insert;
import com.github.q742972035.mysql.binlog.dispatch.annotation.dml.Update;
import com.github.q742972035.mysql.binlog.dispatch.demo.dto.User;

import java.util.Date;
import java.util.Map;

@TableHandler(tableName = "user")
public class UserTableHandler {

    @Insert
    public void insert(@CurrentPosition long cp, @NextPosition long np
    ,@CurrentPosition String scp,@NextPosition int inp){

    }


    @Update
    public void update(@Before Map<String,Object> bm, @After Map<String,Object> am){

    }
}
