package zy.opensource.mysql.binlog.incr.expose.build;

import com.github.shyiko.mysql.binlog.event.QueryEventData;
import zy.opensource.mysql.binlog.incr.expose.build.mysql.hanlder.DefaultConnectionHandler;
import zy.opensource.mysql.binlog.incr.expose.build.mysql.parse.SqlParse;
import zy.opensource.mysql.binlog.incr.expose.build.mysql.sql.SqlFormat;
import zy.opensource.mysql.binlog.incr.expose.build.mysql.sql.SqlAcquire;
import zy.opensource.mysql.binlog.incr.expose.build.mysql.table.Columns;
import zy.opensource.mysql.binlog.incr.expose.utils.CacheUtils;

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
