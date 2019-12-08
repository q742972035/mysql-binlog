package com.github.q742972035.mysql.binlog.dispatch.scan.tablehandler;

import com.github.q742972035.mysql.binlog.dispatch.annotation.CurrentPosition;
import com.github.q742972035.mysql.binlog.dispatch.annotation.NextPosition;
import com.github.q742972035.mysql.binlog.dispatch.annotation.TableHandler;
import com.github.q742972035.mysql.binlog.dispatch.annotation.dml.Insert;


@TableHandler(tableName = "user")
public class UserTableHandler {


    @Insert
    public void insert(String id, @CurrentPosition int cp, @NextPosition String np){

    }

}
