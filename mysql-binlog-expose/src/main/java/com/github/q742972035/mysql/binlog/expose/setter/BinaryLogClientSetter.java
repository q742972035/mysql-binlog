package com.github.q742972035.mysql.binlog.expose.setter;

import com.github.q742972035.mysql.binlog.expose.build.BinaryLogClientBuild;
import com.github.shyiko.mysql.binlog.event.deserialization.EventDeserializer;
import com.github.shyiko.mysql.binlog.network.SSLMode;
import com.github.shyiko.mysql.binlog.network.SSLSocketFactory;
import com.github.shyiko.mysql.binlog.network.SocketFactory;

import java.util.concurrent.ThreadFactory;

/**
 * BinaryLogClient 的 set方法
 *
 * @program: mysql-binlog-incr-expose
 * @description
 * @author: zy
 * @create: 2019-08-02 15:10
 **/
public interface BinaryLogClientSetter {
    BinaryLogClientBuild setHostname(String hostname);
    BinaryLogClientBuild setPort(int port);
    BinaryLogClientBuild setUsername(String username);
    BinaryLogClientBuild setPassword(String password);



    /**
     * 如果为fasle，在最后一个event，client将会断开连接
     * @param blocking blocking mode. If set to false - BinaryLogClient will disconnect after the last event.
     */
    BinaryLogClientBuild setBlocking(boolean blocking);


    BinaryLogClientBuild setSSLMode(SSLMode sslMode);

    /**
     * 在mysql集群中，因为此client充当一个slave,所以serverId必须在集群环境中唯一
     * @param serverId
     */
    BinaryLogClientBuild setServerId(long serverId);

    BinaryLogClientBuild setBinlogFilename(String binlogFilename);

    BinaryLogClientBuild setBinlogPosition(long binlogPosition);

    /**
     * @param gtidSet GTID set (can be an empty string).
     * <p>NOTE #1: Any value but null will switch BinaryLogClient into a GTID mode (this will also set binlogFilename
     * to "" (provided it's null) forcing MySQL to send events starting from the oldest known binlog (keep in mind
     * that connection will fail if gtid_purged is anything but empty (unless
     * {@link #setGtidSetFallbackToPurged(boolean)} is set to true))).
     * <p>NOTE #2: GTID set is automatically updated with each incoming GTID event (provided GTID mode is on).
     * @see #getGtidSet()
     * @see #setGtidSetFallbackToPurged(boolean)
     */
    BinaryLogClientBuild setGtidSet(String gtidSet);

    /**
     * @param gtidSetFallbackToPurged true if gtid_purged should be used as a fallback when gtidSet is set to "" and
     * MySQL server has purged some of the binary logs, false otherwise (default).
     */
    BinaryLogClientBuild setGtidSetFallbackToPurged(boolean gtidSetFallbackToPurged);

    /**
     * @param useBinlogFilenamePositionInGtidMode true if MySQL server should start streaming events from a given
     * {@link #getBinlogFilename()} and {@link #getBinlogPosition()} instead of "the oldest known binlog" when
     * {@link #getGtidSet()} is set, false otherwise (default).
     */
    BinaryLogClientBuild setUseBinlogFilenamePositionInGtidMode(boolean useBinlogFilenamePositionInGtidMode);


    BinaryLogClientBuild setKeepAlive(boolean keepAlive);

    BinaryLogClientBuild setKeepAliveInterval(long keepAliveInterval);

    BinaryLogClientBuild setKeepAliveConnectTimeout(long connectTimeout);

    BinaryLogClientBuild setHeartbeatInterval(long heartbeatInterval);

    BinaryLogClientBuild setConnectTimeout(long connectTimeout);

    BinaryLogClientBuild setEventDeserializer(EventDeserializer eventDeserializer);

    BinaryLogClientBuild setSocketFactory(SocketFactory socketFactory);

    BinaryLogClientBuild setSslSocketFactory(SSLSocketFactory sslSocketFactory);

    BinaryLogClientBuild setThreadFactory(ThreadFactory threadFactory);

    BinaryLogClientBuild setSchema(String schema);
}
