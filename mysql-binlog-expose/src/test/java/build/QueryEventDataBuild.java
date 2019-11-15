package build;

import com.github.shyiko.mysql.binlog.event.QueryEventData;

/**
 * @program: mysql-binlog-incr-expose
 * @description:
 * @author: 张忆
 * @create: 2019-08-02 21:36
 **/
public class QueryEventDataBuild {
    private QueryEventData queryEventData = new QueryEventData();
    public QueryEventData build(){
        return queryEventData;
    }
    public QueryEventDataBuild setErrorCode(int code){
        queryEventData.setErrorCode(code);
        return this;
    }
    public QueryEventDataBuild setThreadId(int id){
        queryEventData.setThreadId(id);
        return this;
    }
    public QueryEventDataBuild setExecutionTime(long executionTime){
        queryEventData.setExecutionTime(executionTime);
        return this;
    }
    public QueryEventDataBuild setDatabase(String db){
        queryEventData.setDatabase(db);
        return this;
    }
    public QueryEventDataBuild setSql(String sql){
        queryEventData.setSql(sql);
        return this;
    }
}
