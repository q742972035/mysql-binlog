package zy.opensource.mysql.binlog.incr.expose.exception;

/**
 * @program: mysql-binlog-incr-expose
 * @description
 * @author: zy
 * @create: 2019-08-13 16:23
 **/
public class EventMergeException extends Exception {
    public EventMergeException(String msg) {
        super(msg);
    }
}
