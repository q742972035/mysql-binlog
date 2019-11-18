package com.github.q742972035.mysql.binlog.dispath;

import com.github.q742972035.mysql.binlog.dispath.batch.handler.DdlEventInfoMergeHandler;
import com.github.q742972035.mysql.binlog.dispath.batch.handler.DmlEventInfoMergeHandler;
import com.github.q742972035.mysql.binlog.dispath.batch.handler.EventInfoMergeHandler;
import com.github.q742972035.mysql.binlog.dispath.exception.IllegalMethodException;
import com.github.q742972035.mysql.binlog.dispath.exception.MethodAnnotationException;
import com.github.q742972035.mysql.binlog.dispath.exception.TableHandlerExistException;
import com.github.q742972035.mysql.binlog.dispath.list.GenerateGroupPrefixName;
import com.github.q742972035.mysql.binlog.dispath.list.ReadWriteLinkedList;
import com.github.q742972035.mysql.binlog.dispath.list.ReadWriteLinkedListGroup;
import com.github.q742972035.mysql.binlog.dispath.list.SubTaskBody;
import com.github.q742972035.mysql.binlog.dispath.list.exception.NoSuchSubTaskException;
import com.github.q742972035.mysql.binlog.dispath.list.exception.PrefixRepetitionException;
import com.github.q742972035.mysql.binlog.dispath.scan.PackageScanner;
import com.github.q742972035.mysql.binlog.dispath.scan.ScanResult;
import com.github.q742972035.mysql.binlog.expose.build.*;
import com.github.q742972035.mysql.binlog.expose.event.ConnectionEventListener;
import com.github.q742972035.mysql.binlog.expose.event.EventInfoMergeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.*;

/**
 * 启动
 */
public class Runner {
    private static final Logger LOGGER = LoggerFactory.getLogger(Runner.class);

    private static final Map<Class, EventInfoMergeHandler> HANDLER_MAP = new HashMap<>();

    static {
        HANDLER_MAP.put(DMLEventInfoMerge.class, new DmlEventInfoMergeHandler());
        HANDLER_MAP.put(DDLEventInfoMerge.class, new DdlEventInfoMergeHandler());
    }

    /**
     * 扫描的包
     */
    private String scanpackage;
    /**
     * expose中的数据库从库客户端构造器
     */
    private BinaryLogClientBuild clientBuild;
    /**
     * 批量执行任务的线程数量(固定)，定义group的queue数量
     */
    private int threadSize = 10;

    /**
     * 扫描结果 可以定制子类
     */
    private ScanResult scanResult;

    /**
     * 在group（有N个queue）中插入任务T,如果有名为AQ的queue中存在同样的任务类型T，则将T放入AQ中，否则从group中获取任务数量最少的MQ，并将T放入MQ中。
     */
    private ReadWriteLinkedListGroup<BaseEventInfoMerge> group;

    private static final List<CloseEventListener> CLOSE_LISTENERS = new ArrayList<>();

    static {
        CLOSE_LISTENERS.add((r, e) -> {
            if (e instanceof TableHandlerExistException
                    || e instanceof IllegalMethodException
                    || e instanceof MethodAnnotationException
                    ) {
                r.setExceptionClose(true);
            }
        });
    }


    public Runner setClientBuild(BinaryLogClientBuild clientBuild) {
        this.clientBuild = clientBuild;
        return this;
    }

    public void addCloseEventListener(CloseEventListener listener) {
        CLOSE_LISTENERS.add(listener);
    }

    public Runner setThreadSize(int threadSize) {
        this.threadSize = threadSize;
        return this;
    }


    public Runner setScanpackage(String scanpackage) {
        this.scanpackage = scanpackage;
        return this;
    }


    public void init() {
        try {
            group = new ReadWriteLinkedListGroup(threadSize, GenerateGroupPrefixName.generator(this));
        } catch (PrefixRepetitionException e) {
            LOGGER.error("", e);
            return;
        }


        List<String> allTaskName = group.getAllTaskName();
        for (String task : allTaskName) {
            group.setRunBack(task, new ReadWriteLinkedList.RunBack<SubTaskBody<BaseEventInfoMerge>>() {
                @Override
                public void result(SubTaskBody<BaseEventInfoMerge> subTaskBody) throws Exception {
                    BaseEventInfoMerge baseEventInfoMerge = subTaskBody.getTask();
                    if (baseEventInfoMerge instanceof DMLEventInfoMerge) {
                        HANDLER_MAP.get(DMLEventInfoMerge.class).handlerEventInfoMerge(baseEventInfoMerge);
                    }
                }
            });
        }
    }

    public Runner setScanResult(ScanResult scanResult) {
        this.scanResult = scanResult;
        return this;
    }

    private DataSource dataSource;

    public Runner setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        return this;
    }

    /**
     * 是否由异常引起程序不启动
     */
    private boolean exceptionClose;

    public Runner setExceptionClose(boolean exceptionClose) {
        this.exceptionClose = exceptionClose;
        return this;
    }

    public void run() throws IOException {
        if (scanResult == null) {
            PackageScanner scanner = new PackageScanner();
            scanner.scan(scanpackage.split(","));
            scanResult = new ScanResult(scanner);
        }
        try {
            scanResult.initContext();
        } catch (Exception e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("", e);
            }
            for (CloseEventListener listener : CLOSE_LISTENERS) {
                listener.close(this, e);
            }
            if (exceptionClose) {
                if (LOGGER.isErrorEnabled()){
                    LOGGER.error("{} closed",getClass().getName());
                }
                return;
            }
        }

        ExposeConfig config = new ExposeConfig();
        config.setDataSource(dataSource);
        // 监听ddl和dml事件
        config.addConnectionEvent(new ConnectionEventListener() {
            @Override
            public void onConnect(boolean connected, BinLogInfo binLogInfo) {

            }
        });
        config.addEventInfoMergeListener(new EventInfoMergeListener() {
            @Override
            public void onEvent(DDLEventInfoMerge ddlEventInfoMerge) {

            }

            @Override
            public void onEvent(DMLEventInfoMerge dmlEventInfoMerge) {
                EventTableInfo lastEventTableInfo = dmlEventInfoMerge.getLastEventTableInfo();
                try {
                    group.excuseTask(new SubTaskBody<>(lastEventTableInfo.getTable(), dmlEventInfoMerge));
                } catch (NoSuchSubTaskException e) {
                    if (LOGGER.isErrorEnabled()) {
                        LOGGER.error("", e);
                    }
                }
            }
        });

        Expose expose = new Expose(config);
        expose.build(clientBuild);
        expose.connect();
    }

    public interface CloseEventListener {
        void close(Runner runner, Exception e);
    }
}
