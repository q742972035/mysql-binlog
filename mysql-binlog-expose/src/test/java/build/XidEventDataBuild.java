package build;

import com.github.shyiko.mysql.binlog.event.XidEventData;

/**
 * @program: mysql-binlog-incr-expose
 * @description:
 * @author: 张忆
 * @create: 2019-08-02 23:55
 **/
public class XidEventDataBuild {
    private XidEventData xidEventData = new XidEventData();
    public XidEventData build(){
        return xidEventData;
    }
    public XidEventDataBuild setXid(long xid){
        xidEventData.setXid(xid);
        return this;
    }
}
