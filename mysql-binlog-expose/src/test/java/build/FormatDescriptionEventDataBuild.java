package build;

import com.github.shyiko.mysql.binlog.event.FormatDescriptionEventData;
import com.github.shyiko.mysql.binlog.event.deserialization.ChecksumType;

/**
 * @program: mysql-binlog-incr-expose
 * @description:
 * @author: 张忆
 * @create: 2019-08-02 21:34
 **/
public class FormatDescriptionEventDataBuild {
    private FormatDescriptionEventData formatDescriptionEventData = new FormatDescriptionEventData();
    public FormatDescriptionEventData build(){
        return formatDescriptionEventData;
    }
    public FormatDescriptionEventDataBuild setChecksumType(ChecksumType checksumType){
        formatDescriptionEventData.setChecksumType(checksumType);
        return this;
    }
    public FormatDescriptionEventDataBuild setDataLength(int dl){
        formatDescriptionEventData.setDataLength(dl);
        return this;
    }
    public FormatDescriptionEventDataBuild setBinlogVersion(int binlogVersion){
        formatDescriptionEventData.setBinlogVersion(binlogVersion);
        return this;
    }
    public FormatDescriptionEventDataBuild setHeaderLength(int headerLength){
        formatDescriptionEventData.setHeaderLength(headerLength);
        return this;
    }
    public FormatDescriptionEventDataBuild setServerVersion(String serverVersion){
        formatDescriptionEventData.setServerVersion(serverVersion);
        return this;
    }

}
