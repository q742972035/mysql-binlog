package com.github.q742972035.mysql.binlog.dispath.scan.tablehandler;

import com.github.q742972035.mysql.binlog.dispath.annotation.CurrentPosition;
import com.github.q742972035.mysql.binlog.dispath.annotation.NextPosition;
import com.github.q742972035.mysql.binlog.dispath.annotation.TableHandler;
import com.github.q742972035.mysql.binlog.dispath.annotation.dml.Insert;
import com.github.q742972035.mysql.binlog.dispath.annotation.dml.Update;
import com.github.q742972035.mysql.binlog.dispath.exception.GenericClassNotFoundException;
import com.github.q742972035.mysql.binlog.dispath.handler.BaseTableHandler;
import com.github.q742972035.mysql.binlog.dispath.scan.dto.User;

import java.util.Arrays;


@TableHandler(tableName = "user")
public class UserTableHandler {


    @Insert
    public void insert(String id, @CurrentPosition int cp, @NextPosition String np){

    }

}
