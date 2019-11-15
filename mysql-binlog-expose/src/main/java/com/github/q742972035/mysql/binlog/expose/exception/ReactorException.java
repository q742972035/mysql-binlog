package com.github.q742972035.mysql.binlog.expose.exception;

import com.github.q742972035.mysql.binlog.expose.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: mysql-binlog-incr-expose
 * @description
 * @author: zy
 * @create: 2019-08-08 11:05
 **/
public class ReactorException extends Exception {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    public ReactorException(Class implementClass, Class... interfaces){
        super(String.format("实现类%s,并没有接口%s。",StringUtils.getClassNames(implementClass),StringUtils.getClassNames(interfaces)));
    }


}
