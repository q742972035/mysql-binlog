package zy.opensource.mysql.binlog.incr.expose.build;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.event.deserialization.EventDeserializer;
import com.github.shyiko.mysql.binlog.network.SSLMode;
import com.github.shyiko.mysql.binlog.network.SSLSocketFactory;
import com.github.shyiko.mysql.binlog.network.SocketFactory;
import zy.opensource.mysql.binlog.incr.expose.setter.BinaryLogClientSetter;

import java.util.concurrent.ThreadFactory;

/**
 * @program: mysql-binlog-incr-expose
 * @description
 * @author: zy
 * @create: 2019-08-02 15:21
 **/
public class BinaryLogClientBuild implements BinaryLogClientSetter {
    private BinaryLogClient binaryLogClient;
    private int port = 3306;
    private String hostname = "127.0.0.1";
    private String username;
    private String password;
    private String schema;
    private boolean blocking = true;
    private SSLMode sslMode = SSLMode.DISABLED;
    private long serverId = 65535;
    private String binlogFilename;
    private long binlogPosition = 4;
    private String gtidSet;
    private boolean gtidSetFallbackToPurged;
    private boolean useBinlogFilenamePositionInGtidMode;
    private boolean keepAlive;
    private long keepAliveInterval;
    private long connectTimeout;
    private long heartbeatInterval;
    private EventDeserializer eventDeserializer = new EventDeserializer();
    private SocketFactory socketFactory;
    private SSLSocketFactory sslSocketFactory;
    private ThreadFactory threadFactory;

    BinaryLogClient build() {
        if (binaryLogClient != null) {
            return binaryLogClient;
        }
        binaryLogClient = new BinaryLogClient(hostname, port, schema, username, password);
        binaryLogClient.setBlocking(blocking);
        binaryLogClient.setSSLMode(sslMode);
        binaryLogClient.setServerId(serverId);
        binaryLogClient.setBinlogFilename(binlogFilename);
        binaryLogClient.setBinlogPosition(binlogPosition);
        binaryLogClient.setGtidSet(gtidSet);
        binaryLogClient.setGtidSetFallbackToPurged(gtidSetFallbackToPurged);
        binaryLogClient.setUseBinlogFilenamePositionInGtidMode(useBinlogFilenamePositionInGtidMode);
        binaryLogClient.setKeepAlive(keepAlive);
        binaryLogClient.setKeepAliveInterval(keepAliveInterval);
        binaryLogClient.setKeepAliveConnectTimeout(connectTimeout);
        binaryLogClient.setHeartbeatInterval(heartbeatInterval);
        binaryLogClient.setConnectTimeout(connectTimeout);
        binaryLogClient.setEventDeserializer(eventDeserializer);
        binaryLogClient.setSocketFactory(socketFactory);
        binaryLogClient.setSslSocketFactory(sslSocketFactory);
        binaryLogClient.setThreadFactory(threadFactory);
        return binaryLogClient;
    }

    @Override
    public BinaryLogClientBuild setHostname(String hostname) {
        this.hostname = hostname;
        return this;
    }

    @Override
    public BinaryLogClientBuild setPort(int port) {
        this.port = port;
        return this;
    }

    @Override
    public BinaryLogClientBuild setUsername(String username) {
        this.username = username;
        return this;
    }

    @Override
    public BinaryLogClientBuild setPassword(String password) {
        this.password = password;
        return this;
    }

    @Override
    public BinaryLogClientBuild setBlocking(boolean blocking) {
        this.blocking = blocking;
        return this;
    }

    @Override
    public BinaryLogClientBuild setSSLMode(SSLMode sslMode) {
        this.sslMode = sslMode;
        return this;
    }

    @Override
    public BinaryLogClientBuild setServerId(long serverId) {
        this.serverId = serverId;
        return this;
    }

    @Override
    public BinaryLogClientBuild setBinlogFilename(String binlogFilename) {
        this.binlogFilename = binlogFilename;
        return this;
    }

    @Override
    public BinaryLogClientBuild setBinlogPosition(long binlogPosition) {
        this.binlogPosition = binlogPosition;
        return this;
    }

    @Override
    public BinaryLogClientBuild setGtidSet(String gtidSet) {
        this.gtidSet = gtidSet;
        return this;
    }

    @Override
    public BinaryLogClientBuild setGtidSetFallbackToPurged(boolean gtidSetFallbackToPurged) {
        this.gtidSetFallbackToPurged = gtidSetFallbackToPurged;
        return this;
    }

    @Override
    public BinaryLogClientBuild setUseBinlogFilenamePositionInGtidMode(boolean useBinlogFilenamePositionInGtidMode) {
        this.useBinlogFilenamePositionInGtidMode = useBinlogFilenamePositionInGtidMode;
        return this;
    }

    @Override
    public BinaryLogClientBuild setKeepAlive(boolean keepAlive) {
        this.keepAlive = keepAlive;
        return this;
    }

    @Override
    public BinaryLogClientBuild setKeepAliveInterval(long keepAliveInterval) {
        this.keepAliveInterval = keepAliveInterval;
        return this;
    }

    @Override
    public BinaryLogClientBuild setKeepAliveConnectTimeout(long connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    @Override
    public BinaryLogClientBuild setHeartbeatInterval(long heartbeatInterval) {
        this.heartbeatInterval = heartbeatInterval;
        return this;
    }

    @Override
    public BinaryLogClientBuild setConnectTimeout(long connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    @Override
    public BinaryLogClientBuild setEventDeserializer(EventDeserializer eventDeserializer) {
        this.eventDeserializer = eventDeserializer;
        return this;
    }

    @Override
    public BinaryLogClientBuild setSocketFactory(SocketFactory socketFactory) {
        this.socketFactory = socketFactory;
        return this;
    }

    @Override
    public BinaryLogClientBuild setSslSocketFactory(SSLSocketFactory sslSocketFactory) {
        this.sslSocketFactory = sslSocketFactory;
        return this;
    }

    @Override
    public BinaryLogClientBuild setThreadFactory(ThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
        return this;
    }

    @Override
    public BinaryLogClientBuild setSchema(String schema) {
        this.schema = schema;
        return this;
    }
}
