package zy.opensource.mysql.binlog.incr.expose.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zy.opensource.mysql.binlog.incr.expose.utils.StringUtils;

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
