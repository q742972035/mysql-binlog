package build;


import com.github.q742972035.mysql.binlog.incr.expose.build.*;
import com.github.q742972035.mysql.binlog.incr.expose.event.ConnectionEventListener;
import com.github.q742972035.mysql.binlog.incr.expose.event.EventInfoMergeListener;
import com.github.q742972035.mysql.binlog.incr.expose.extension.EventInfoExtension;
import org.junit.Test;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class ExposeTest1 extends AbstractTest {

    @Test
    public void doMain() throws InterruptedException, IOException {


        ExposeConfig config = new ExposeConfig();
        config.addConnectionEvent(new ConnectionEventListener() {
            @Override
            public void onConnect(boolean connected, BinLogInfo binLogInfo) {
                // 连接时
                if (connected){
                    System.out.println(binLogInfo.getBinlogFileName()+"----"+binLogInfo.getBinlogPosition());
                    // 关闭时
                }else {
                    System.out.println(binLogInfo.getBinlogFileName()+"----"+binLogInfo.getBinlogPosition());
                }
            }
        });
        config.addEventInfoMergeListener(new EventInfoMergeListener() {
            // 初始化时执行
            @Override
            public void onEvent(BaseEventInfoMerge eventInfoMerge) {
                EventInfoWrap parse = eventInfoMerge.parse();
                Iterator<EventInfoExtension> iterator = parse.iterator();
                while (iterator.hasNext()){
                    EventInfoExtension next = iterator.next();
                    next.getCurrentStep();// 获取步骤数
                    next.getStepCount();// 获取总步骤数
                    next.getEventInfo();// 获取步骤EventHeader EventData nextPosition 和 SqlType
                }

            }

            // 执行非InnoDb的DML语句时触发
            public void onEvent(CommitDMLEventInfoMerge commitDMLEventInfoMerge) {
            }

            // 执行DML语句触发
            public void onEvent(DMLEventInfoMerge dmlEventInfoMerge) {
                System.out.println(dmlEventInfoMerge.getCurrentPosition());
                List<EventTableInfo> historyEventTableInfos = dmlEventInfoMerge.getHistoryEventTableInfos();
                for (EventTableInfo eventTableInfo : historyEventTableInfos) {
                    System.out.println(eventTableInfo.getTable()); // 获取表名
                    System.out.println(eventTableInfo.getSqlType()); // 获取DML信息
                    System.out.println(eventTableInfo.getDatabase()); // 获取db的名字
                    for (List<TableElement> tableElements: eventTableInfo.getTableElements()) {
                        for (TableElement tableElement : tableElements) {
                            System.out.println(tableElement); //获取表信息
                        }
                    }
                }
                System.out.println(dmlEventInfoMerge.getNextPosition());
            }

            // 执行DDL语句触发
            @Override
            public void onEvent(DDLEventInfoMerge ddlEventInfoMerge) {
            }



            // 执行InnoDb的DML语句时触发
            @Override
            public void onEvent(XidDMLEventInfoMerge xidDMLEventInfoMerge) {
//                List<EventTableInfo> historyEventTableInfos = xidDMLEventInfoMerge.getHistoryEventTableInfos();
//                for (EventTableInfo eventTableInfo : historyEventTableInfos) {
//                    System.out.println(eventTableInfo.getTable()); // 获取表名
//                    System.out.println(eventTableInfo.getSqlType()); // 获取DML信息
//                    System.out.println(eventTableInfo.getDatabase()); // 获取db的名字
//                    for (List<TableElement> tableElements: eventTableInfo.getTableElements()) {
//                        for (TableElement tableElement : tableElements) {
//                            System.out.println(tableElement); //获取表信息
//                        }
//                    }
//                }
            }
        });


        BinaryLogClientBuild build = new BinaryLogClientBuild();
        build.setHostname("106.12.138.136")
                .setUsername("award3")
                .setPassword("87654321")
                .setSchema("t_move");
        // 设置binlog文件信息
//                .setBinlogFilename("mysql-bin.000006")
        // 设置binlog position的位置
//                .setBinlogPosition(510328438);

        Expose expose = new Expose(config).build(build);

        new Thread(() -> {
            try {
                expose.connect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

//        Thread.sleep(10000);
//        expose.disconnect();



        synchronized (this){
            this.wait();
        }
    }
}
