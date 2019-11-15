package com.github.q742972035.mysql.binlog.expose.build;

import com.github.q742972035.mysql.binlog.expose.build.mysql.hanlder.DefaultConnectionHandler;
import com.github.q742972035.mysql.binlog.expose.build.mysql.parse.SqlParse;
import com.github.q742972035.mysql.binlog.expose.build.mysql.sql.SqlAcquire;
import com.github.q742972035.mysql.binlog.expose.build.mysql.sql.SqlFormat;
import com.github.q742972035.mysql.binlog.expose.build.mysql.table.Columns;
import com.github.q742972035.mysql.binlog.expose.utils.CacheUtils;
import com.github.q742972035.mysql.binlog.expose.build.mysql.hanlder.DefaultConnectionHandler;
import com.github.q742972035.mysql.binlog.expose.build.mysql.parse.SqlParse;
import com.github.q742972035.mysql.binlog.expose.build.mysql.sql.SqlAcquire;
import com.github.q742972035.mysql.binlog.expose.build.mysql.sql.SqlFormat;
import com.github.q742972035.mysql.binlog.expose.build.mysql.table.Columns;
import com.github.q742972035.mysql.binlog.expose.utils.CacheUtils;
import com.github.shyiko.mysql.binlog.event.QueryEventData;

import java.lang.reflect.Method;

/**
 * @program: mysql-binlog-incr-expose
 * @description
 * @author: zy
 * @create: 2019-08-15 16:11
 **/
public class DDLEventInfoMerge extends BaseEventInfoMerge {

    private static Method EXCUTE_METHOD;

    static {
        try {
            EXCUTE_METHOD = DefaultConnectionHandler.class.getMethod("excute", String.class, Class.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }


    public DDLEventInfoMerge(ExposeConfig exposeConfig) {
        super(exposeConfig);
    }



    private String database;
    private String tableName;

    public String getDatabase() {
        return database;
    }

    public String getTableName() {
        return tableName;
    }

    @Override
    protected void beforeMergeForDdlCreate(EventInfo eventInfo) {
        doCreateAlterDrop(eventInfo);
    }

    @Override
    protected void beforeMergeForDdlTruncate(EventInfo eventInfo) {
    }

    @Override
    protected void beforeMergeForDdlAlter(EventInfo eventInfo) {
        doCreateAlterDrop(eventInfo);
    }

    @Override
    protected void beforeMergeForDdlDrop(EventInfo eventInfo) {
        doCreateAlterDrop(eventInfo);
    }

    private void doCreateAlterDrop(EventInfo eventInfo){
        QueryEventData queryEventData = (QueryEventData) eventInfo.getEventData();
        String sql = queryEventData.getSql();
        SqlParse sqlParse = new SqlParse(sql);
        this.database = sqlParse.getDababase() == null ? queryEventData.getDatabase() : sqlParse.getDababase();
        this.tableName = sqlParse.getTableName();
        CacheUtils.clear(new CacheUtils.Info(EXCUTE_METHOD, SqlAcquire.getSql(SqlFormat.COLUMN_SQL, database, tableName), Columns.class));
    }
}
