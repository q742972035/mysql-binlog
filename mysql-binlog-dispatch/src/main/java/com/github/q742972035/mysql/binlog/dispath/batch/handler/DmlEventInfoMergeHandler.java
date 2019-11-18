package com.github.q742972035.mysql.binlog.dispath.batch.handler;

import com.github.q742972035.mysql.binlog.dispath.RunnerDispath;
import com.github.q742972035.mysql.binlog.dispath.annotation.dml.Delete;
import com.github.q742972035.mysql.binlog.dispath.annotation.dml.Insert;
import com.github.q742972035.mysql.binlog.dispath.annotation.dml.Update;
import com.github.q742972035.mysql.binlog.dispath.scan.ClassLoaderContext;
import com.github.q742972035.mysql.binlog.dispath.scan.ClassLoaderContextFactory;
import com.github.q742972035.mysql.binlog.dispath.scan.MethodMetadata;
import com.github.q742972035.mysql.binlog.expose.build.DMLEventInfoMerge;
import com.github.q742972035.mysql.binlog.expose.build.EventTableInfo;
import com.github.q742972035.mysql.binlog.expose.build.TableElement;
import com.github.q742972035.mysql.binlog.expose.global.Global;
import com.github.q742972035.mysql.binlog.expose.type.sql.SqlType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class DmlEventInfoMergeHandler implements EventInfoMergeHandler<DMLEventInfoMerge> {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void handlerEventInfoMerge(DMLEventInfoMerge dmlEventInfoMerge) {
        try {
            // 获取表信息
            EventTableInfo lastEventTableInfo = dmlEventInfoMerge.getLastEventTableInfo();
            String database = lastEventTableInfo.getDatabase();
            String table = lastEventTableInfo.getTable();
            Global.CURRENT_DB.set(database);
            Global.CURRENT_TB.set(table);
            // 获取到数据变化的信息
            List<List<TableElement>> tableElementLists = lastEventTableInfo.getTableElements();

            for (List<TableElement> tableElementList : tableElementLists) {
                ClassLoaderContext classLoaderContext = ClassLoaderContextFactory.getInstance(lastEventTableInfo.getTable());
                if (classLoaderContext == null) {
                    continue;
                }
                SqlType sqlType = lastEventTableInfo.getSqlType();
                MethodMetadata methodMetadata = null;
                if (sqlType == SqlType.DML.INSERT) {
                    methodMetadata = classLoaderContext.getMethodMetadataByAnnotation(Insert.class);
                } else if (sqlType == SqlType.DML.UPDATE) {
                    methodMetadata = classLoaderContext.getMethodMetadataByAnnotation(Update.class);
                } else if (sqlType == SqlType.DML.DELETE) {
                    methodMetadata = classLoaderContext.getMethodMetadataByAnnotation(Delete.class);
                }
                if (methodMetadata != null) {
                    RunnerDispath runnerDispath = new RunnerDispath(classLoaderContext.getSource(), methodMetadata, tableElementList, dmlEventInfoMerge.getCurrentPosition(), dmlEventInfoMerge.getNextPosition());
                    runnerDispath.dispath();
                }
            }
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error("", e);
            }
//            if (LOGGER.isErrorEnabled()) {
//                LOGGER.error(String.format("发生异常，当前Position:%s,下一个position:%s", currentPosition, dmlEventInfoMerge.getNextPosition()), e);
//            }
//            if (skiperror) {
//            } else {
//                System.exit(0);
//            }
        }
    }
}
