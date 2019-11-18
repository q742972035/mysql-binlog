package com.github.q742972035.mysql.binlog.dispath.scan.tablehandler;


import com.github.q742972035.mysql.binlog.dispath.annotation.TableHandler;
import com.github.q742972035.mysql.binlog.dispath.annotation.dml.Delete;

@TableHandler(tableName = "admin")
public class AdminTableHandler {



    @Delete
    public void doDelete(){

    }
}
