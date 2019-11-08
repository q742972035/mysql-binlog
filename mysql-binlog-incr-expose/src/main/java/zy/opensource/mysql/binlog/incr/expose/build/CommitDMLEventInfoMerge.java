package zy.opensource.mysql.binlog.incr.expose.build;


/**
 * @program: mysql-binlog-incr-expose
 * @description
 * @author: zy
 * @create: 2019-08-15 16:13
 **/
public class CommitDMLEventInfoMerge extends DMLEventInfoMerge {
    public CommitDMLEventInfoMerge(ExposeConfig exposeConfig) {
        super(exposeConfig);
    }

    public CommitDMLEventInfoMerge(DMLEventInfoMerge other) {
        super(other);
    }

}
