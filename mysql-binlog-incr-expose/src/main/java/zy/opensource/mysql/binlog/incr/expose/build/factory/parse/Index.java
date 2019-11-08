package zy.opensource.mysql.binlog.incr.expose.build.factory.parse;

import java.io.Serializable;

/**
 * @program: mysql-binlog-incr-expose
 * @description
 * @author: zy
 * @create: 2019-08-21 15:46
 **/
public class Index {
    Long index;
    Serializable serializable;

    public Index(Long index, Serializable serializable) {
        this.index = index;
        this.serializable = serializable;
    }

    public Long getIndex() {
        return index;
    }

    public Serializable getSerializable() {
        return serializable;
    }
}
