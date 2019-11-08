package zy.opensource.mysql.binlog.incr.expose.build.mysql.parse;

import zy.opensource.mysql.binlog.incr.expose.cons.BaseConst;

/**
 * 仅仅简单的解析ddl语句的db和tablename
 *
 * @program: mysql-binlog-incr-expose
 * @description
 * @author: zy
 * @create: 2019-08-20 20:40
 **/
public class SqlParse {

    private String sql;

    private String db;
    private String tb;

    public SqlParse(String sql) {
        this.sql = sql.trim();
        String lower = this.sql.toLowerCase();


        int index = -1;
        if (lower.startsWith(BaseConst.CREATE_LOW)) {
            index = lower.indexOf('(');
        } else if (lower.startsWith(BaseConst.ALTER_LOW)) {
            index = lower.indexOf("modify");
        }
        if (index > -1) {
            this.sql = this.sql.substring(0, index);
            // 获取table的下标
            int tableIndex = lower.indexOf("table");
            this.sql = this.sql.substring(tableIndex + 5).trim();

            String[] split = this.sql.split("\\.");
            for (int i = 0; i < split.length; i++) {
                split[i] = split[i].replace("`", "");
            }
            if (split.length > 1) {
                db = split[0];
                tb = split[1];
            } else {
                tb = split[0];
            }
        }
    }

    public String getDababase() {
        return db;
    }

    public String getTableName() {
        return tb;
    }


}
