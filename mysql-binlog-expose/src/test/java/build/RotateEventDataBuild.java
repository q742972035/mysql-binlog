package build;

import com.github.shyiko.mysql.binlog.event.RotateEventData;

/**
 * @program: mysql-binlog-incr-expose
 * @description:
 * @author: 张忆
 * @create: 2019-08-02 21:32
 **/
public class RotateEventDataBuild {
    private RotateEventData rotateEventData = new RotateEventData();
    public RotateEventData build(){
        return rotateEventData;
    }
    public RotateEventDataBuild setBinlogFilename(String filename){
        rotateEventData.setBinlogFilename(filename);
        return this;
    }
    public RotateEventDataBuild setBinlogPosition(long bs){
        rotateEventData.setBinlogPosition(bs);
        return this;
    }
}
