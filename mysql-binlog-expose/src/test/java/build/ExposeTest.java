package build;

import com.alibaba.druid.pool.DruidDataSource;
import com.github.q742972035.mysql.binlog.expose.build.*;
import com.github.q742972035.mysql.binlog.expose.build.mysql.hanlder.DefaultConnectionHandler;
import com.github.q742972035.mysql.binlog.expose.event.ConnectionEventListener;
import com.github.q742972035.mysql.binlog.expose.event.EventInfoMergeListener;
import com.github.q742972035.mysql.binlog.expose.event.FailureEventListener;
import com.github.q742972035.mysql.binlog.expose.event.type.FailureType;
import com.github.q742972035.mysql.binlog.expose.extension.EventInfoExtension;
import com.github.q742972035.mysql.binlog.expose.type.sql.SqlType;
import com.github.q742972035.mysql.binlog.expose.utils.StreamUtils;
import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.event.Event;
import com.mysql.cj.MysqlType;
import org.junit.Before;
import org.junit.Test;
import utils.BasePropertiesUtils;


import javax.sql.DataSource;
import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import static org.assertj.core.api.Assertions.*;

public class ExposeTest extends AbstractTest {

    private static final String CREATE_TEST_BINLOG_INNO = "druid/create_test_binlog_inno.sql";
    private static final String CREATE_TEST_BINLOG_MYSIAM = "druid/create_test_binlog_mysiam.sql";

    private static final String DML_INSERT = "druid/dml_insert.sql";
    private static final String DML_UPDATE = "druid/dml_update.sql";
    private static final String TRANSACTION = "druid/transaction.sql";


    public String read(String url) throws IOException {
        InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream(url);
        byte[] bytes = StreamUtils.copyToByteArray(resourceAsStream);
        return new String(bytes);
    }

    DruidDataSource dataSource = new DruidDataSource();

    @Before
    public void upset() throws IOException {
        dataSource = new DruidDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl(BasePropertiesUtils.getKey("jdbc.url"));
        dataSource.setUsername(BasePropertiesUtils.getKey("jdbc.username"));
        dataSource.setPassword(BasePropertiesUtils.getKey("jdbc.password"));
    }


    @Test
    public void testSimpleStart() throws InterruptedException, IOException {
        ExposeConfig config = new ExposeConfig();
        config.setDataSource(dataSource);
        BinaryLogClientBuild build = new BinaryLogClientBuild();
        build.setBlocking(true)
                .setHostname(BasePropertiesUtils.getKey("jdbc.host"))
                .setUsername(BasePropertiesUtils.getKey("jdbc.username"))
                .setPassword(BasePropertiesUtils.getKey("jdbc.password"))
                .setSchema("");
        Expose expose = new Expose(config).build(build);
        expose.connect();

        synchronized (this){
            wait();
        }
    }


    @Test
    public void useDatabaseTest() throws IOException, InterruptedException {
        String createDbIfNotExists = BasePropertiesUtils.createDbNotExists("t_move");
        String use = BasePropertiesUtils.use("t_move");
        String droptb1 = BasePropertiesUtils.dropTb("test_binlog_inno");
        String droptb2 = BasePropertiesUtils.dropTb("test_binlog_mysiam");
        String createtb1 = read(CREATE_TEST_BINLOG_INNO);
        String createtb2 = read(CREATE_TEST_BINLOG_MYSIAM);
        // 创建库，use库，drop表
        execute(Arrays.asList(createDbIfNotExists, use, droptb1, droptb2), dataSource);

        Object lock = new Object();
        ExporseAdapter adapter = new ExporseAdapter();
        Thread thread = new ExposeThread(lock, adapter, dataSource,
                new Back() {
                    @Override
                    public void backBase(List<BaseEventInfoMerge> baseEventInfoMerges) {
                        assertThat(baseEventInfoMerges.size()).isEqualTo(1);

                        BaseEventInfoMerge baseEventInfoMerge = baseEventInfoMerges.get(0);

                        assertThat(baseEventInfoMerge.size()).isEqualTo(2);

                        Iterator<EventInfoExtension> iterator = baseEventInfoMerge.parse().iterator();
                        EventInfoExtension extension = iterator.next();
                        assertThat(extension.getEventInfo().getSqlType()).isEqualTo(SqlType.BaseInfo.ROTATE);
                        assertThat(extension.getCurrentStep()).isEqualTo(1);
                        assertThat(extension.getStepCount()).isEqualTo(2);
                        assertThat(extension.isFirstStep()).isEqualTo(true);
                        assertThat(extension.isLastStep()).isEqualTo(false);
                        assertThat(extension.isUseful()).isEqualTo(false);

                        extension = iterator.next();
                        assertThat(extension.getEventInfo().getSqlType()).isEqualTo(SqlType.BaseInfo.FORMATD_ESCRIPTION);
                        assertThat(extension.getCurrentStep()).isEqualTo(2);
                        assertThat(extension.getStepCount()).isEqualTo(2);
                        assertThat(extension.isFirstStep()).isEqualTo(false);
                        assertThat(extension.isLastStep()).isEqualTo(true);
                        assertThat(extension.isUseful()).isEqualTo(true);


                    }

                    @Override
                    public void backDml(List<DMLEventInfoMerge> dmlEventInfoMerges) {
                        assertThat(dmlEventInfoMerges.size()).isEqualTo(4);

                        BaseEventInfoMerge baseEventInfoMerge = dmlEventInfoMerges.get(0);

                        assertThat(baseEventInfoMerge.size()).isEqualTo(4);

                        Iterator<EventInfoExtension> iterator = baseEventInfoMerge.parse().iterator();
                        EventInfoExtension extension = iterator.next();
                        assertThat(extension.getEventInfo().getSqlType()).isEqualTo(SqlType.DML.BEGIN);
                        assertThat(extension.getCurrentStep()).isEqualTo(1);
                        assertThat(extension.getStepCount()).isEqualTo(4);
                        assertThat(extension.isFirstStep()).isEqualTo(true);
                        assertThat(extension.isLastStep()).isEqualTo(false);
                        assertThat(extension.isUseful()).isEqualTo(false);

                        extension = iterator.next();
                        assertThat(extension.getEventInfo().getSqlType()).isEqualTo(SqlType.DML.TABLE_MAP);
                        assertThat(extension.getCurrentStep()).isEqualTo(2);
                        assertThat(extension.getStepCount()).isEqualTo(4);
                        assertThat(extension.isFirstStep()).isEqualTo(false);
                        assertThat(extension.isLastStep()).isEqualTo(false);
                        assertThat(extension.isUseful()).isEqualTo(false);

                        extension = iterator.next();
                        assertThat(extension.getEventInfo().getSqlType()).isEqualTo(SqlType.DML.INSERT);
                        assertThat(extension.getCurrentStep()).isEqualTo(3);
                        assertThat(extension.getStepCount()).isEqualTo(4);
                        assertThat(extension.isFirstStep()).isEqualTo(false);
                        assertThat(extension.isLastStep()).isEqualTo(false);
                        assertThat(extension.isUseful()).isEqualTo(true);

                        extension = iterator.next();
                        assertThat(extension.getEventInfo().getSqlType()).isEqualTo(SqlType.DML.XID);
                        assertThat(extension.getCurrentStep()).isEqualTo(4);
                        assertThat(extension.getStepCount()).isEqualTo(4);
                        assertThat(extension.isFirstStep()).isEqualTo(false);
                        assertThat(extension.isLastStep()).isEqualTo(true);
                        assertThat(extension.isUseful()).isEqualTo(false);


                        List<EventTableInfo> historyEventTableInfos = ((DMLEventInfoMerge) baseEventInfoMerge).getHistoryEventTableInfos();

                        assertThat(historyEventTableInfos.size()).isEqualTo(1);

                        EventTableInfo eventTableInfo = historyEventTableInfos.get(0);

                        assertThat(eventTableInfo.getDatabase()).isEqualTo("t_move");
                        assertThat(eventTableInfo.getTable()).isEqualTo("test_binlog_inno");

                        List<List<TableElement>> tableElements = eventTableInfo.getTableElements();

                        assertThat(tableElements.size()).isEqualTo(2);


                        assertThat(tableElements.get(0).get(0).getIndex()).isEqualTo(1).isEqualTo(tableElements.get(1).get(0).getIndex());
                        assertThat(tableElements.get(0).get(1).getIndex()).isEqualTo(2).isEqualTo(tableElements.get(1).get(1).getIndex());

                        assertThat(tableElements.get(0).get(0).getColumns()).isEqualTo(tableElements.get(1).get(0).getColumns());


                        baseEventInfoMerge = dmlEventInfoMerges.get(3);

                        assertThat(baseEventInfoMerge.size()).isEqualTo(4);

                        iterator = baseEventInfoMerge.parse().iterator();
                        extension = iterator.next();
                        assertThat(extension.getEventInfo().getSqlType()).isEqualTo(SqlType.DML.BEGIN);
                        assertThat(extension.getCurrentStep()).isEqualTo(1);
                        assertThat(extension.getStepCount()).isEqualTo(4);
                        assertThat(extension.isFirstStep()).isEqualTo(true);
                        assertThat(extension.isLastStep()).isEqualTo(false);
                        assertThat(extension.isUseful()).isEqualTo(false);

                        extension = iterator.next();
                        assertThat(extension.getEventInfo().getSqlType()).isEqualTo(SqlType.DML.TABLE_MAP);
                        assertThat(extension.getCurrentStep()).isEqualTo(2);
                        assertThat(extension.getStepCount()).isEqualTo(4);
                        assertThat(extension.isFirstStep()).isEqualTo(false);
                        assertThat(extension.isLastStep()).isEqualTo(false);
                        assertThat(extension.isUseful()).isEqualTo(false);

                        extension = iterator.next();
                        assertThat(extension.getEventInfo().getSqlType()).isEqualTo(SqlType.DML.UPDATE);
                        assertThat(extension.getCurrentStep()).isEqualTo(3);
                        assertThat(extension.getStepCount()).isEqualTo(4);
                        assertThat(extension.isFirstStep()).isEqualTo(false);
                        assertThat(extension.isLastStep()).isEqualTo(false);
                        assertThat(extension.isUseful()).isEqualTo(true);

                        extension = iterator.next();
                        assertThat(extension.getEventInfo().getSqlType()).isEqualTo(SqlType.DML.COMMIT);
                        assertThat(extension.getCurrentStep()).isEqualTo(4);
                        assertThat(extension.getStepCount()).isEqualTo(4);
                        assertThat(extension.isFirstStep()).isEqualTo(false);
                        assertThat(extension.isLastStep()).isEqualTo(true);
                        assertThat(extension.isUseful()).isEqualTo(false);
                    }

                    @Override
                    public void backDdl(List<DDLEventInfoMerge> ddlEventInfoMerges) {
                        assertThat(ddlEventInfoMerges.size()).isEqualTo(2);

                        DDLEventInfoMerge ddlEventInfoMerge = ddlEventInfoMerges.get(0);

                        assertThat(ddlEventInfoMerge.size()).isEqualTo(1);

                        Iterator<EventInfoExtension> iterator = ddlEventInfoMerge.parse().iterator();
                        EventInfoExtension extension = iterator.next();
                        assertThat(extension.getEventInfo().getSqlType()).isEqualTo(SqlType.DDL.CREATE);
                        assertThat(extension.getCurrentStep()).isEqualTo(1);
                        assertThat(extension.getStepCount()).isEqualTo(1);
                        assertThat(extension.isFirstStep()).isEqualTo(true);
                        assertThat(extension.isLastStep()).isEqualTo(true);
                        assertThat(extension.isUseful()).isEqualTo(true);

                        assertThat(ddlEventInfoMerge.getDatabase()).isEqualTo("t_move");
                        assertThat(ddlEventInfoMerge.getTableName()).isEqualTo("test_binlog_inno");

                    }

                });

        // 通过线程执行expose
        thread.start();
        synchronized (lock) {
            lock.wait();
        }

        execute(Arrays.asList(createtb1, createtb2), dataSource);

        List<String> dmlInsertSqls = BasePropertiesUtils.getExecuteSqls(DML_INSERT);
        List<String> dmlUpdateSqls = BasePropertiesUtils.getExecuteSqls(DML_UPDATE);

        execute(dmlInsertSqls, dataSource);

        Thread.sleep(3000);

        execute(dmlUpdateSqls, dataSource);

        // 防止update执行的binlog没有返回就关了
        Thread.sleep(1000);
        Expose expose = adapter.getExpose();
        expose.disconnect();


        Thread.sleep(360000000);
    }


    @Test
    public void testTransaction() throws IOException, InterruptedException {
        String createDbIfNotExists = BasePropertiesUtils.createDbNotExists("t_move");
        String use = BasePropertiesUtils.use("t_move");
        String droptb = BasePropertiesUtils.dropTb("test_binlog_inno");
        String createtb = read(CREATE_TEST_BINLOG_INNO);
        execute(Arrays.asList(createDbIfNotExists, use, droptb, createtb), dataSource);


        List<String> executeTransactionSql = BasePropertiesUtils.getExecuteSqls(TRANSACTION);
        Object lock = new Object();
        ExporseAdapter adapter = new ExporseAdapter();
        Thread thread = new ExposeThread(lock, adapter, dataSource,
                new Back() {
                    @Override
                    public void backBase(List<BaseEventInfoMerge> baseEventInfoMerge) {
                        assertThat(baseEventInfoMerge.size()).isEqualTo(1);

                        BaseEventInfoMerge merge = baseEventInfoMerge.get(0);
                        assertThat(merge.size()).isEqualTo(2);
                        assertThat(merge.canMerge()).isFalse();

                        Iterator<EventInfoExtension> extensionIterator = merge.parse().iterator();
                        EventInfoExtension extension = extensionIterator.next();
                        assertThat(extension.getEventInfo().getSqlType()).isEqualTo(SqlType.BaseInfo.ROTATE);
                        assertThat(extension.getCurrentStep()).isEqualTo(1);
                        assertThat(extension.getStepCount()).isEqualTo(2);
                        assertThat(extension.isFirstStep()).isEqualTo(true);
                        assertThat(extension.isLastStep()).isEqualTo(false);
                        assertThat(extension.isUseful()).isEqualTo(false);

                        extensionIterator = merge.parse().iterator();
                        extension = extensionIterator.next();
                        assertThat(extension.getEventInfo().getSqlType()).isEqualTo(SqlType.BaseInfo.FORMATD_ESCRIPTION);
                        assertThat(extension.getCurrentStep()).isEqualTo(2);
                        assertThat(extension.getStepCount()).isEqualTo(2);
                        assertThat(extension.isFirstStep()).isEqualTo(false);
                        assertThat(extension.isLastStep()).isEqualTo(true);
                        assertThat(extension.isUseful()).isEqualTo(true);
                    }


                    @Override
                    public void backXid(List<XidDMLEventInfoMerge> xidDMLEventInfoMerges) {
                        XidDMLEventInfoMerge xidDMLEventInfoMerge = xidDMLEventInfoMerges.get(0);
                        assertThat(xidDMLEventInfoMerge.size()).isEqualTo(6);

                        Iterator<EventInfoExtension> iterator = xidDMLEventInfoMerge.parse().iterator();
                        EventInfoExtension extension = iterator.next();
                        assertThat(extension.getEventInfo().getSqlType()).isEqualTo(SqlType.DML.BEGIN);
                        assertThat(extension.getCurrentStep()).isEqualTo(1);
                        assertThat(extension.getStepCount()).isEqualTo(6);
                        assertThat(extension.isFirstStep()).isEqualTo(true);
                        assertThat(extension.isLastStep()).isEqualTo(false);
                        assertThat(extension.isUseful()).isEqualTo(false);

                        extension = iterator.next();
                        assertThat(extension.getEventInfo().getSqlType()).isEqualTo(SqlType.DML.TABLE_MAP);
                        assertThat(extension.getCurrentStep()).isEqualTo(2);
                        assertThat(extension.getStepCount()).isEqualTo(6);
                        assertThat(extension.isFirstStep()).isEqualTo(false);
                        assertThat(extension.isLastStep()).isEqualTo(false);
                        assertThat(extension.isUseful()).isEqualTo(false);

                        extension = iterator.next();
                        assertThat(extension.getEventInfo().getSqlType()).isEqualTo(SqlType.DML.INSERT);
                        assertThat(extension.getCurrentStep()).isEqualTo(3);
                        assertThat(extension.getStepCount()).isEqualTo(6);
                        assertThat(extension.isFirstStep()).isEqualTo(false);
                        assertThat(extension.isLastStep()).isEqualTo(false);
                        assertThat(extension.isUseful()).isEqualTo(true);

                        extension = iterator.next();
                        assertThat(extension.getEventInfo().getSqlType()).isEqualTo(SqlType.DML.TABLE_MAP);
                        assertThat(extension.getCurrentStep()).isEqualTo(4);
                        assertThat(extension.getStepCount()).isEqualTo(6);
                        assertThat(extension.isFirstStep()).isEqualTo(false);
                        assertThat(extension.isLastStep()).isEqualTo(false);
                        assertThat(extension.isUseful()).isEqualTo(false);

                        extension = iterator.next();
                        assertThat(extension.getEventInfo().getSqlType()).isEqualTo(SqlType.DML.UPDATE);
                        assertThat(extension.getCurrentStep()).isEqualTo(5);
                        assertThat(extension.getStepCount()).isEqualTo(6);
                        assertThat(extension.isFirstStep()).isEqualTo(false);
                        assertThat(extension.isLastStep()).isEqualTo(false);
                        assertThat(extension.isUseful()).isEqualTo(true);

                        extension = iterator.next();
                        assertThat(extension.getEventInfo().getSqlType()).isEqualTo(SqlType.DML.XID);
                        assertThat(extension.getCurrentStep()).isEqualTo(6);
                        assertThat(extension.getStepCount()).isEqualTo(6);
                        assertThat(extension.isFirstStep()).isEqualTo(false);
                        assertThat(extension.isLastStep()).isEqualTo(true);
                        assertThat(extension.isUseful()).isEqualTo(false);

                        List<EventTableInfo> eventTableInfos = xidDMLEventInfoMerge.getHistoryEventTableInfos();

                        EventTableInfo insertInfos = eventTableInfos.get(0);
                        assertThat(insertInfos.getSqlType()).isEqualTo(SqlType.DML.INSERT);
                        assertThat(insertInfos.getTableElements().size()).isEqualTo(3);

                        EventTableInfo updateInfos = eventTableInfos.get(1);
                        assertThat(updateInfos.getSqlType()).isEqualTo(SqlType.DML.UPDATE);
                        assertThat(updateInfos.getTableElements().size()).isEqualTo(2);
                    }

                });


        thread.start();
        synchronized (lock) {
            lock.wait();
        }
        execute(executeTransactionSql, dataSource);
        Thread.sleep(2000);
        adapter.getExpose().disconnect();

        Thread.sleep(100000000);
    }


    @Test
    public void insertAlterTest() throws IOException, InterruptedException {
        final String createPath = "druid/alter/create_test_alter.sql";
        final String alterPath = "druid/alter/alter_test_alter.sql";
        final String firstInsertPath = "druid/alter/first_insert.sql";
        final String secInsertPath = "druid/alter/second_insert.sql";

        String createDbIfNotExists = BasePropertiesUtils.createDbNotExists("t_move");
        String use = BasePropertiesUtils.use("t_move");
        String droptb = BasePropertiesUtils.dropTb("test_alter");
        String createtb = read(createPath);
        execute(Arrays.asList(createDbIfNotExists, use, droptb, createtb), dataSource);


        Object lock = new Object();
        ExporseAdapter adapter = new ExporseAdapter();
        Thread thread = new ExposeThread(lock, adapter, dataSource,
                new Back() {
                    @Override
                    public void backDml(List<DMLEventInfoMerge> dmlEventInfoMerges) {
                        assertThat(dmlEventInfoMerges.size()).isEqualTo(3);
//
                        assertThat(dmlEventInfoMerges.get(0).getHistoryEventTableInfos().get(0).getTableElements().get(0).get(0).getMysqlType()).isEqualTo(MysqlType.INT);
                        assertThat(dmlEventInfoMerges.get(1).getHistoryEventTableInfos().get(0).getTableElements().get(0).get(0).getMysqlType()).isEqualTo(MysqlType.INT);
                        assertThat(dmlEventInfoMerges.get(2).getHistoryEventTableInfos().get(0).getTableElements().get(0).get(0).getMysqlType()).isEqualTo(MysqlType.INT);
//
                        assertThat(dmlEventInfoMerges.get(0).getHistoryEventTableInfos().get(0).getTableElements().get(0).get(1).getMysqlType()).isEqualTo(MysqlType.INT);
                        assertThat(dmlEventInfoMerges.get(1).getHistoryEventTableInfos().get(0).getTableElements().get(0).get(1).getMysqlType()).isEqualTo(MysqlType.VARCHAR);
                        assertThat(dmlEventInfoMerges.get(2).getHistoryEventTableInfos().get(0).getTableElements().get(0).get(1).getMysqlType()).isEqualTo(MysqlType.VARCHAR);

                        assertThatThrownBy(() -> dmlEventInfoMerges.get(0).getHistoryEventTableInfos().get(0).getTableElements().get(0).get(2).getMysqlType()).hasMessage("Index: 2, Size: 2");
                        assertThat(dmlEventInfoMerges.get(1).getHistoryEventTableInfos().get(0).getTableElements().get(0).get(2).getMysqlType()).isEqualTo(MysqlType.DATETIME);
                        assertThat(dmlEventInfoMerges.get(2).getHistoryEventTableInfos().get(0).getTableElements().get(0).get(2).getMysqlType()).isEqualTo(MysqlType.DATETIME);

                        System.out.println("================all is correct==================");
                    }
                });
        thread.start();
        synchronized (lock) {
            lock.wait();
        }

        String fiSql = read(firstInsertPath);
        String alterSql = read(alterPath);
        List<String> siSqls = BasePropertiesUtils.getExecuteSqls(secInsertPath);
        execute(Arrays.asList(fiSql), dataSource);
        Thread.sleep(1000); // 如果不等待，ddl在数据库执行时，没有来得及通知，到时上面语句查询到的列信息是alter之后
        execute(Arrays.asList(alterSql), dataSource);
        execute(siSqls, dataSource);


        Thread.sleep(2000);
        adapter.getExpose().disconnect();

        Thread.sleep(100000000);

    }


    @Test
    public void testPosition() throws IOException, InterruptedException, SQLException {
        final String createPath = "druid/alter/create_test_alter.sql";
        String createDbIfNotExists = BasePropertiesUtils.createDbNotExists("t_move");
        String use = BasePropertiesUtils.use("t_move");
        String droptb = BasePropertiesUtils.dropTb("test_alter");
        String createtb = read(createPath);
        execute(Arrays.asList(createDbIfNotExists, use, droptb,createtb), dataSource);


        ExposeConfig config = new ExposeConfig();
        config.setDataSource(dataSource);
        DefaultConnectionHandler connectionHandler = new DefaultConnectionHandler(config);


        final int loopTime = 3;

        Object lock = new Object();
        ExporseAdapter adapter = new ExporseAdapter();
        List<BinLog> binLogs = new ArrayList<>();
        Thread thread = new ExposeThread(lock, adapter, dataSource, new Back() {
            @Override
            public void backDml(List<DMLEventInfoMerge> dmlEventInfoMerges) {
                assertThat(dmlEventInfoMerges.size()).isEqualTo(loopTime);
                assertThat(dmlEventInfoMerges.get(0).getNextPosition()).isEqualTo(binLogs.get(0).getPosition());
                assertThat(dmlEventInfoMerges.get(1).getNextPosition()).isEqualTo(binLogs.get(1).getPosition());
                assertThat(dmlEventInfoMerges.get(2).getNextPosition()).isEqualTo(binLogs.get(2).getPosition());

                System.out.println("================all is correct==================");
            }
        });
        thread.start();
        synchronized (lock) {
            lock.wait();
        }



        final String firstInsertPath = "druid/alter/first_insert.sql";
        String insertSql = read(firstInsertPath);

        for (int i = 0; i < loopTime; i++) {
            execute(Arrays.asList(insertSql),dataSource);
            Thread.sleep(1000);
            binLogs.add(connectionHandler.execute("SHOW MASTER STATUS", BinLog.class).get(0));
        }

        adapter.getExpose().disconnect();

        Thread.sleep(10000000);
    }



    @Test
    public void useTest() throws IOException {
        ExposeConfig exposeConfig = new ExposeConfig();
        exposeConfig.addEventInfoMergeListener(new EventInfoMergeListener() {
            @Override
            public void onEvent(DDLEventInfoMerge ddlEventInfoMerge) {

            }

            @Override
            public void onEvent(DMLEventInfoMerge dmlEventInfoMerge) {

            }
        }).setDataSource(dataSource).addConnectionEvent(new ConnectionEventListener() {
            @Override
            public void onConnect(boolean connected, BinLogInfo binLogInfo) {

            }
        }).addFailureEvent(new FailureEventListener() {
            @Override
            public void onFailureEvent(FailureType failureType, BinLogInfo binLogInfo, Exception e) {

            }
        });

        BinaryLogClientBuild build = new BinaryLogClientBuild();
        build.setBlocking(true).setUsername("").setPassword("").setSchema("");
        new Expose(exposeConfig).build(build).connect();
    }

    @Test
    public void testOther() throws IOException, InterruptedException {
        BinaryLogClient client = new BinaryLogClient(BasePropertiesUtils.getKey("jdbc.host"), 3306,
                BasePropertiesUtils.getKey("jdbc.username"), BasePropertiesUtils.getKey("jdbc.password"));

        client.registerEventListener(new BinaryLogClient.EventListener() {
            @Override
            public void onEvent(Event event) {
            }
        });

        client.connect();

        Thread.sleep(10000000);
    }


    static class ExporseAdapter {
        private Expose expose;

        public Expose getExpose() {
            return expose;
        }

        public void setExpose(Expose expose) {
            this.expose = expose;
        }
    }


    static class ExposeThread extends Thread {
        private Object lock;
        private ExporseAdapter adapter;
        private Back back;
        private DataSource dataSource;

        public ExposeThread(Object lock, ExporseAdapter adapter, DataSource dataSource, Back back) {
            this.lock = lock;
            this.adapter = adapter;
            this.back = back;
            this.dataSource = dataSource;
        }

        @Override
        public void run() {
            List<BaseEventInfoMerge> baseEventInfoMerges = new ArrayList<>();
            List<DMLEventInfoMerge> dmlEventInfoMerges = new ArrayList<>();
            List<DDLEventInfoMerge> ddlEventInfoMerges = new ArrayList<>();
            List<XidDMLEventInfoMerge> xidDMLEventInfoMerges = new ArrayList<>();
            List<CommitDMLEventInfoMerge> commitDMLEventInfoMerges = new ArrayList<>();

            ExposeConfig config = new ExposeConfig()
                    .addEventInfoMergeListener(new EventInfoMergeListener() {
                        @Override
                        public void onEvent(BaseEventInfoMerge eventInfoMerge) {
                            baseEventInfoMerges.add(eventInfoMerge);
                        }

                        @Override
                        public void onEvent(CommitDMLEventInfoMerge commitDMLEventInfoMerge) {
                            commitDMLEventInfoMerges.add(commitDMLEventInfoMerge);
                        }

                        @Override
                        public void onEvent(XidDMLEventInfoMerge xidDMLEventInfoMerge) {
                            xidDMLEventInfoMerges.add(xidDMLEventInfoMerge);
                        }

                        @Override
                        public void onEvent(DDLEventInfoMerge ddlEventInfoMerge) {
                            ddlEventInfoMerges.add(ddlEventInfoMerge);
                        }

                        @Override
                        public void onEvent(DMLEventInfoMerge dmlEventInfoMerge) {
                            dmlEventInfoMerges.add(dmlEventInfoMerge);
                        }

                    })
                    .addConnectionEvent((connected, binLogInfo) -> {
                        if (connected) {
                            synchronized (lock) {
                                lock.notifyAll();
                            }
                        } else {
                            if (back != null) {
                                back.backBase(baseEventInfoMerges);
                                back.backDml(dmlEventInfoMerges);
                                back.backDdl(ddlEventInfoMerges);
                                back.backXid(xidDMLEventInfoMerges);
                                back.backCommit(commitDMLEventInfoMerges);
                            }
                        }
                    });
            config.setDataSource(dataSource);

            try {
                Expose expose = new Expose(config);
                adapter.setExpose(expose);
                expose.build(new BinaryLogClientBuild()
                        .setHostname(BasePropertiesUtils.getKey("jdbc.host"))
                        .setUsername(BasePropertiesUtils.getKey("jdbc.username"))
                        .setPassword(BasePropertiesUtils.getKey("jdbc.password"))
                );
                expose.connect();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    interface Back {
        default void backBase(List<BaseEventInfoMerge> baseEventInfoMerges) {
        }

        default void backDml(List<DMLEventInfoMerge> dmlEventInfoMerges) {
        }

        default void backDdl(List<DDLEventInfoMerge> ddlEventInfoMerges) {
        }

        default void backXid(List<XidDMLEventInfoMerge> xidDMLEventInfoMerges) {
        }

        default void backCommit(List<CommitDMLEventInfoMerge> commitDMLEventInfoMerges) {
        }
    }

    public void execute(List<String> sqls, DataSource dataSource) {
        try (
                Connection connection = dataSource.getConnection();
                Statement statement = connection.createStatement();
        ) {
            for (String sql : sqls) {
                statement.addBatch(sql);
            }
            statement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
