package com.github.q742972035.mysql.binlog.expose.build;

import com.github.q742972035.mysql.binlog.expose.build.mysql.hanlder.DefaultConnectionHandler;
import com.github.q742972035.mysql.binlog.expose.build.mysql.sql.SqlAcquire;
import com.github.q742972035.mysql.binlog.expose.build.mysql.sql.SqlFormat;
import com.github.q742972035.mysql.binlog.expose.build.mysql.table.Variables;
import com.github.q742972035.mysql.binlog.expose.event.DefaultFailureEventListener;
import com.github.q742972035.mysql.binlog.expose.utils.ReflectionUtils;
import com.github.q742972035.mysql.binlog.expose.event.DefaultBinaryLogEventListener;
import com.github.q742972035.mysql.binlog.expose.event.DefaultConnectionEventListener;
import com.github.q742972035.mysql.binlog.expose.event.DefaultFailureEventListener;
import com.github.q742972035.mysql.binlog.expose.event.type.FailureType;
import com.github.q742972035.mysql.binlog.expose.utils.ReflectionUtils;
import com.github.shyiko.mysql.binlog.BinaryLogClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

/**
 * 封装了mysql-binlog的总输出
 *
 * @program: mysql-binlog-incr-expose
 * @description
 * @author: zy
 * @create: 2019-08-02 15:55
 **/
public class Expose {
    private Logger logger = LoggerFactory.getLogger(getClass());
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

        try {
            ReflectionUtils.copyField(build,"hostname",this.exposeConfig,"defaultUrl");
            ReflectionUtils.copyField(build,"port",this.exposeConfig,"defaultPort");
            ReflectionUtils.copyField(build,"username",this.exposeConfig,"defaultUserName");
            ReflectionUtils.copyField(build,"password",this.exposeConfig,"defaultPassword");
            ReflectionUtils.copyField(build,"schema",this.exposeConfig,"schema");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return this;
    }


    public void connect() throws IOException {
        if (!checkVariables("binlog_format","row")){
            throw new IllegalStateException("请检测mysql的binlog属性，其中binlog_format必须是row");
        }
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

    private boolean checkVariables(String key,String value){
        DefaultConnectionHandler connectionHandler = new DefaultConnectionHandler(ExposeContext.getConfig());
        try {
            List<Variables> variablesList = connectionHandler.execute(SqlAcquire.getSql(SqlFormat.VARIABLES_SQL, key), Variables.class);
            variablesList = Optional.of(variablesList).orElseThrow(()->new IllegalStateException("variables 不可以为null"));
            if (variablesList.isEmpty()){
                throw new IllegalStateException("无法查询属性key:"+key);
            }
            Variables variables = variablesList.get(0);
            if (value.equalsIgnoreCase(variables.getValue())){
                return true;
            }
        }catch (Exception e){
            if (logger.isErrorEnabled()){
                logger.error("",e);
            }
            return false;
        }
        return false;
    }

    public void disconnect() throws IOException {
        this.client.disconnect();
    }
}
