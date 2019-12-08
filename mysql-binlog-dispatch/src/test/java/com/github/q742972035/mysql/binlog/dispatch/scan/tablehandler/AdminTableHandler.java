package com.github.q742972035.mysql.binlog.dispatch.scan.tablehandler;


import com.github.q742972035.mysql.binlog.dispatch.annotation.TableHandler;
import com.github.q742972035.mysql.binlog.dispatch.annotation.dml.Delete;

@TableHandler(tableName = "admin")
public class AdminTableHandler {



    @Delete
    public void doDelete(){

    }
}
