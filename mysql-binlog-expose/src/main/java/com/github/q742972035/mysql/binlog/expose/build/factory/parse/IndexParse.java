package com.github.q742972035.mysql.binlog.expose.build.factory.parse;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

/**
 * @program: mysql-binlog-incr-expose
 * @description
 * @author: zy
 * @create: 2019-08-21 15:46
 **/
public class IndexParse {
    Iterator<Long> iterator;
    Serializable[] serializables;
    int index;

    public IndexParse(List<Long> indexList, Serializable[] serializables) {
        this.iterator = indexList.iterator();
        this.serializables = serializables;
    }

    public Index index() {
        try {
            Long next = iterator.next();
            return new Index(next, serializables[index++]);
        } catch (Exception e) {
            return null;
        }

    }


}
