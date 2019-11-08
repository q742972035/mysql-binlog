package zy.opensource.mysql.binlog.incr.expose.build;

/**
 * @program: mysql-binlog-incr-expose
 * @description
 * @author: zy
 * @create: 2019-08-15 16:13
 **/
public class XidDMLEventInfoMerge extends DMLEventInfoMerge{
    public XidDMLEventInfoMerge(ExposeConfig exposeConfig) {
        super(exposeConfig);
    }

    public XidDMLEventInfoMerge(DMLEventInfoMerge other) {
        super(other);
    }


}
