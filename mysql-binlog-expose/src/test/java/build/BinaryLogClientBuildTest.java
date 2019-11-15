package build;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.event.*;
import com.github.shyiko.mysql.binlog.event.deserialization.ChecksumType;
import com.github.shyiko.mysql.binlog.event.deserialization.ColumnType;
import org.junit.Before;
import org.junit.Test;
import print.PrintAppend;
import zy.opensource.mysql.binlog.incr.expose.build.BinaryLogClientBuild;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.Map;

import static print.PrintUtil.pln;

/**
 * @program: mysql-binlog-incr-expose
 * @description
 * @author: zy
 * @create: 2019-08-02 15:34
 **/
public class BinaryLogClientBuildTest {

    private BinaryLogClient client;
    private Method buildMethod;

    @Before
    public void setUp() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        BinaryLogClientBuild binaryLogClientBuild = new BinaryLogClientBuild()
                .setHostname("*")
                .setUsername("*")
                .setPassword("*")
                .setSchema("*")
//                .setBinlogFilename("mysql-bin.000002")
//                .setBinlogPosition(4005)
                .setBlocking(false);

        buildMethod = BinaryLogClientBuild.class.getDeclaredMethod("build");
        buildMethod.setAccessible(true);
        client = (BinaryLogClient) buildMethod.invoke(binaryLogClientBuild);
    }

    @Test
    public void test() throws IOException {
        client.registerEventListener(new BinaryLogClient.EventListener() {
            @Override
            public void onEvent(Event event) {
                EventHeader header = event.getHeader();
                EventData data = event.getData();

                EventType eventType = header.getEventType();
                System.out.println(eventType + "----" + data.getClass().getName());
                System.out.println("header----" + header.getClass().getName());

                switch (eventType) {
                    // 刷新binlog时,读取binlog配置
                    case ROTATE:
                        RotateEventData rotateEventData = (RotateEventData) data;
                        String binlogFilename = rotateEventData.getBinlogFilename();
                        long binlogPosition = rotateEventData.getBinlogPosition();
//                        System.out.println(String.format("binlogFilename:%s,\r\nbinlogFilename:%s",binlogFilename,binlogPosition));
                        pln("binlogFilename:%s binlogFilename:%s", binlogFilename, binlogPosition);
                        break;
                    // 读取mysqld的描述
                    case FORMAT_DESCRIPTION:
                        FormatDescriptionEventData formatDescriptionEventData = (FormatDescriptionEventData) data;
                        ChecksumType checksumType = formatDescriptionEventData.getChecksumType();
                        int dataLength = formatDescriptionEventData.getDataLength();
                        int binlogVersion = formatDescriptionEventData.getBinlogVersion();
                        int headerLength = formatDescriptionEventData.getHeaderLength();
                        String serverVersion = formatDescriptionEventData.getServerVersion();
                        pln("checksumType:%s dataLength:%s binlogVersion:%s headerLength:%s serverVersion:%s", checksumType, dataLength, binlogVersion, headerLength, serverVersion);
                        break;
                    // 创建表时(ddl create)，修改表结构(ddl alter)
                    case QUERY:
                        QueryEventData queryEventData = (QueryEventData) data;
                        int errorCode = queryEventData.getErrorCode();
                        long threadId = queryEventData.getThreadId();
                        long executionTime = queryEventData.getExecutionTime();
                        String database = queryEventData.getDatabase();
                        String sql = queryEventData.getSql();
                        pln("errorCode:%s threadId:%s executionTime:%s database:%s sql:%s", errorCode, threadId, executionTime, database, sql);
                        break;
                    case TABLE_MAP:
                        TableMapEventData tableMapEventData = (TableMapEventData) data;
                        long tableId = tableMapEventData.getTableId();
                        database = tableMapEventData.getDatabase();
                        String table = tableMapEventData.getTable();
                        byte[] columnTypes = tableMapEventData.getColumnTypes();
                        int[] columnMetadata = tableMapEventData.getColumnMetadata();
                        BitSet columnNullability = tableMapEventData.getColumnNullability();
                        PrintAppend printAppend = new PrintAppend();
                        printAppend.setFirst("tableId:%s ").setSecond(tableId)
                                .setFirst("database:%s ").setSecond(database)
                                .setFirst("table:%s ").setSecond(table);
                        for (int i = 0; i < columnTypes.length; i++) {
                            int index = i + 1;
                            printAppend.setFirst("columnType[" + index + "]:%s ").setSecond(ColumnType.byCode(columnTypes[i]));
                        }
                        printAppend.setFirst("columnNullability:%s ").setSecond(columnNullability).pln();
                        break;
                    case EXT_WRITE_ROWS:
                        WriteRowsEventData writeRowsEventData = (WriteRowsEventData) data;
                        tableId = writeRowsEventData.getTableId();
                        BitSet includedColumns = writeRowsEventData.getIncludedColumns();
                        List<Serializable[]> rows = writeRowsEventData.getRows();
                        String s = "";
                        for (Serializable[] row : rows) {
                            s += Arrays.toString(row);
                        }
                        pln("tableId:%s includedColumns:%s rows:%s", tableId, includedColumns, s);
                        break;
                    case XID:
                        XidEventData xidEventData = (XidEventData) data;
                        long xid = xidEventData.getXid();
                        pln("xid:%s ",xid);
                        break;
                    case EXT_UPDATE_ROWS:
                        UpdateRowsEventData updateRowsEventData = (UpdateRowsEventData) data;
                        tableId = updateRowsEventData.getTableId();
                        BitSet includedColumnsBeforeUpdate = updateRowsEventData.getIncludedColumnsBeforeUpdate();
                        includedColumns = updateRowsEventData.getIncludedColumns();
                        List<Map.Entry<Serializable[], Serializable[]>> mapRows = updateRowsEventData.getRows();
                        printAppend = new PrintAppend();
                        printAppend.setFirst("tableId:%s ").setSecond(tableId);
                        printAppend.setFirst("includedColumnsBeforeUpdate:%s ").setSecond(includedColumnsBeforeUpdate);
                        printAppend.setFirst("includedColumns:%s ").setSecond(includedColumns);
                        s = "";
                        for (Map.Entry<Serializable[], Serializable[]> row : mapRows) {
                            s+="before["+Arrays.toString(row.getKey())+"],";
                            s+="after["+Arrays.toString(row.getValue())+"];";
                        }
                        printAppend.setFirst("rows:%s ").setSecond(s).pln();
                        break;
                    case EXT_DELETE_ROWS:
                        DeleteRowsEventData deleteRowsEventData = (DeleteRowsEventData) data;
                        includedColumns = deleteRowsEventData.getIncludedColumns();
                        tableId = deleteRowsEventData.getTableId();
                        rows = deleteRowsEventData.getRows();
                        s = "";
                        for (Serializable[] row : rows) {
                            s += Arrays.toString(row);
                        }
                        pln("tableId:%s includedColumns:%s rows:%s", tableId, includedColumns, s);
                        break;
                    default:
                        System.out.println("other---------------------------------->" + data);
                        break;
                }


            }
        });
        client.connect();
    }
}
