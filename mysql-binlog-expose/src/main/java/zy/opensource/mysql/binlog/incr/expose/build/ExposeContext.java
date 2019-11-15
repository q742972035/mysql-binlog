package zy.opensource.mysql.binlog.incr.expose.build;

/**
 * @program: mysql-binlog-incr-expose
 * @description
 * @author: zy
 * @create: 2019-08-20 15:25
 **/
public class ExposeContext {
    private static ExposeConfig config;

    public static void setConfig(ExposeConfig config) {
        ExposeContext.config = config;
    }

    public static ExposeConfig getConfig() {
        return config;
    }
}
