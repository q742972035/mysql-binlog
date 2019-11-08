package zy.opensource.mysql.binlog.incr.expose.build;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import zy.opensource.mysql.binlog.incr.expose.event.DefaultBinaryLogEventListener;
import zy.opensource.mysql.binlog.incr.expose.event.DefaultConnectionEventListener;
import zy.opensource.mysql.binlog.incr.expose.event.DefaultFailureEventListener;
import zy.opensource.mysql.binlog.incr.expose.event.type.FailureType;

import java.io.IOException;

/**
 * 封装了mysql-binlog的总输出
 *
 * @program: mysql-binlog-incr-expose
 * @description
 * @author: zy
 * @create: 2019-08-02 15:55
 **/
public class Expose {
    private ExposeConfig exposeConfig;
    private DefaultBinaryLogEventListener binaryLogEventListener;
    private DefaultConnectionEventListener connectionEventListener;
    private DefaultFailureEventListener failureEventListener;


    public Expose(ExposeConfig config) {
        exposeConfig = config;
        ExposeContext.setConfig(config);
        connectionEventListener = new DefaultConnectionEventListener(exposeConfig);
        binaryLogEventListener = new DefaultBinaryLogEventListener(exposeConfig);
        failureEventListener = new DefaultFailureEventListener(exposeConfig);
    }


    private BinaryLogClient client;


    public Expose build(BinaryLogClientBuild build) {
        if (this.client == null) {
            this.client = build.build();
        }
        return this;
    }


    public void connect() throws IOException {
        this.client.registerEventListener(event -> binaryLogEventListener.onEvent(event));
        this.client.registerLifecycleListener(new BinaryLogClient.LifecycleListener() {
            @Override
            public void onConnect(BinaryLogClient client) {
                connectionEventListener.onConnect(true, new BinLogInfo(client.getBinlogFilename(), client.getBinlogPosition()));
            }

            @Override
            public void onCommunicationFailure(BinaryLogClient client, Exception ex) {
                failureEventListener.onFailureEvent(FailureType.COMMUNICATION_FAILURE, new BinLogInfo(client.getBinlogFilename(), client.getBinlogPosition()), ex);
            }

            @Override
            public void onEventDeserializationFailure(BinaryLogClient client, Exception ex) {
                failureEventListener.onFailureEvent(FailureType.EVENT_DESERIALIZATION_FAILURE, new BinLogInfo(client.getBinlogFilename(), client.getBinlogPosition()), ex);
            }

            @Override
            public void onDisconnect(BinaryLogClient client) {
                connectionEventListener.onConnect(false, new BinLogInfo(client.getBinlogFilename(), client.getBinlogPosition()));
            }
        });
        this.client.connect();

    }

    public void disconnect() throws IOException {
        this.client.disconnect();
    }
}
