package build;

import com.github.shyiko.mysql.binlog.event.DeleteRowsEventData;
import com.github.shyiko.mysql.binlog.event.Event;
import com.github.shyiko.mysql.binlog.event.EventHeaderV4;
import com.github.shyiko.mysql.binlog.event.TableMapEventData;
import com.github.shyiko.mysql.binlog.event.deserialization.ChecksumType;
import org.junit.Before;
import org.junit.Test;
import zy.opensource.mysql.binlog.incr.expose.build.EventInfo;
import zy.opensource.mysql.binlog.incr.expose.build.BaseEventInfoMerge;
import zy.opensource.mysql.binlog.incr.expose.build.EventInfoWrap;
import zy.opensource.mysql.binlog.incr.expose.build.ExposeConfig;
import zy.opensource.mysql.binlog.incr.expose.cons.BaseConst;
import zy.opensource.mysql.binlog.incr.expose.exception.EventInfoCreateException;
import zy.opensource.mysql.binlog.incr.expose.exception.EventMergeException;
import zy.opensource.mysql.binlog.incr.expose.extension.EventInfoExtension;
import zy.opensource.mysql.binlog.incr.expose.type.sql.SqlType;
import zy.opensource.mysql.binlog.incr.expose.utils.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @program: mysql-binlog-incr-expose
 * @description:
 * @author: 张忆
 * @create: 2019-08-04 02:30
 **/
public class BaseEventInfoMergeTest {


    private BaseEventInfoMerge eventInfoMerge;

    @Before
    public void setup() {
        eventInfoMerge = new BaseEventInfoMerge(new ExposeConfig());
    }

    public void reset() {
        setup();
    }

    public void reset(ExposeConfig config) {
        eventInfoMerge = new BaseEventInfoMerge(config);
    }

    @Test
    public void testDDLCreate() throws EventInfoCreateException, EventMergeException {
        reset();


        // 测试create
        eventInfoMerge.merge(new EventInfo(new Event(new EventHeaderV4(), new QueryEventDataBuild()
                .setSql("CREATE ...")
                .build())));

        assertEquals(eventInfoMerge.size(), 1);
        assertEquals(eventInfoMerge.canMerge(), false);

        eventInfoMerge.merge(new EventInfo(new Event(new EventHeaderV4(), new QueryEventDataBuild()
                .setSql("CREATE ")
                .build())));

        assertEquals(eventInfoMerge.size(), 1);
        assertEquals(eventInfoMerge.canMerge(), false);

        reset();


        // 测试正确的create步骤
        System.out.println("=========================correct steps=================================================");
        reset();
        eventInfoMerge.merge(new EventInfo(new Event(new EventHeaderV4(), new RotateEventDataBuild().setBinlogFilename("fi").setBinlogPosition(22L).build())));


        assertEquals(eventInfoMerge.size(), 1);
        assertEquals(eventInfoMerge.canMerge(), true);

        eventInfoMerge.merge(new EventInfo(new Event(new EventHeaderV4(), new FormatDescriptionEventDataBuild().setDataLength(92).setChecksumType(ChecksumType.CRC32).setBinlogVersion(4).setServerVersion("4").build())));

        assertEquals(eventInfoMerge.size(), 2);
        assertEquals(eventInfoMerge.canMerge(), false);

        eventInfoMerge.merge(new EventInfo(new Event(new EventHeaderV4(), new FormatDescriptionEventDataBuild().setDataLength(92).setChecksumType(ChecksumType.CRC32).setBinlogVersion(4).setServerVersion("4").build())));

        assertEquals(eventInfoMerge.size(), 2);
        assertEquals(eventInfoMerge.canMerge(), false);


        reset();
        EventInfo eventInfo;
        eventInfoMerge.merge(eventInfo = new EventInfo(new Event(new EventHeaderV4(), new QueryEventDataBuild().setErrorCode(0).setThreadId(3).setExecutionTime(0).setDatabase("db").setSql("CREATE TABLE `InnoDB_create` (\n" +
                "`id`  int NULL AUTO_INCREMENT ,\n" +
                "PRIMARY KEY (`id`)\n" +
                ")\n" +
                "ENGINE=InnoDB").build())));

        assertEquals(eventInfoMerge.size(), 1);
        assertEquals(eventInfoMerge.canMerge(), false);


        EventInfoWrap parse = eventInfoMerge.parse();
        Iterator<EventInfoExtension> iterator = parse.iterator();
        EventInfoExtension next = iterator.next();

        assertEquals(next.getEventInfo(), eventInfo);
        assertEquals(next.isUseful(), true);
        assertEquals(next.isLastStep(), true);
        assertEquals(next.isFirstStep(), true);
        assertEquals(next.getCurrentStep(), 1);
        assertEquals(next.getStepCount(), 1);

    }

    @Test
    public void testDDLAlter() throws EventInfoCreateException, EventMergeException {
        eventInfoMerge.merge(new EventInfo(new Event(new EventHeaderV4(), new QueryEventDataBuild()
                .setErrorCode(0)
                .setThreadId(3)
                .setExecutionTime(0)
                .setDatabase("t_move")
                .setSql("alTer ...")
                .build())));

        assertEquals(eventInfoMerge.size(), 1);
        assertEquals(eventInfoMerge.canMerge(), false);


        eventInfoMerge.merge(new EventInfo(new Event(new EventHeaderV4(), new QueryEventDataBuild()
                .setErrorCode(0)
                .setThreadId(3)
                .setExecutionTime(0)
                .setDatabase("t_move")
                .setSql("alter ...")
                .build())));

        assertEquals(eventInfoMerge.size(), 1);
        assertEquals(eventInfoMerge.canMerge(), false);


        EventInfoWrap parse = eventInfoMerge.parse();
        Iterator<EventInfoExtension> iterator = parse.iterator();
        EventInfoExtension next = iterator.next();

        assertEquals(next.isUseful(), true);
        assertEquals(next.isLastStep(), true);
        assertEquals(next.isFirstStep(), true);
        assertEquals(next.getCurrentStep(), 1);
        assertEquals(next.getStepCount(), 1);
    }


    @Test
    public void testDDLDrop() throws EventInfoCreateException, EventMergeException {
        eventInfoMerge.merge(new EventInfo(new Event(new EventHeaderV4(), new QueryEventDataBuild()
                .setErrorCode(0)
                .setThreadId(3)
                .setExecutionTime(0)
                .setDatabase("t_move")
                .setSql("DROP TABLE")
                .build())));

        assertEquals(eventInfoMerge.size(), 1);
        assertEquals(eventInfoMerge.canMerge(), false);
        eventInfoMerge.merge(new EventInfo(new Event(new EventHeaderV4(), new QueryEventDataBuild()
                .setErrorCode(0)
                .setThreadId(3)
                .setExecutionTime(0)
                .setDatabase("t_move")
                .setSql("DROP TABLE")
                .build())));


        assertEquals(eventInfoMerge.size(), 1);
        assertEquals(eventInfoMerge.canMerge(), false);


        EventInfoWrap parse = eventInfoMerge.parse();
        Iterator<EventInfoExtension> iterator = parse.iterator();
        EventInfoExtension next = iterator.next();

        assertEquals(next.isUseful(), true);
        assertEquals(next.isLastStep(), true);
        assertEquals(next.isFirstStep(), true);
        assertEquals(next.getCurrentStep(), 1);
        assertEquals(next.getStepCount(), 1);
    }


    @Test
    public void testTransaction() throws EventInfoCreateException, EventMergeException {
        reset();

        EventInfo eventInfo1;
        EventInfo eventInfo2;

        eventInfoMerge.merge(eventInfo1 = new EventInfo(new Event(new EventHeaderV4(), new RotateEventDataBuild().setBinlogFilename("fi").setBinlogPosition(22L).build())));
        eventInfoMerge.merge(eventInfo2 = new EventInfo(new Event(new EventHeaderV4(), new FormatDescriptionEventDataBuild().setDataLength(92).setChecksumType(ChecksumType.CRC32).setBinlogVersion(4).setServerVersion("4").build())));

        assertEquals(eventInfoMerge.size(), 2);
        assertEquals(eventInfoMerge.canMerge(), false);

        Iterator<EventInfoExtension> iterator = eventInfoMerge.parse().iterator();
        EventInfoExtension next = iterator.next();
        assertEquals(next.getEventInfo(), eventInfo1);
        assertEquals(next.getStepCount(), 2);
        assertEquals(next.getCurrentStep(), 1);
        assertEquals(next.isUseful(), false);
        assertEquals(next.isFirstStep(), true);
        assertEquals(next.isLastStep(), false);

        next = iterator.next();
        assertEquals(next.getEventInfo(), eventInfo2);
        assertEquals(next.getStepCount(), 2);
        assertEquals(next.getCurrentStep(), 2);
        assertEquals(next.isUseful(), true);
        assertEquals(next.isFirstStep(), false);
        assertEquals(next.isLastStep(), true);


        reset();

        EventInfo eventInfo3;
        EventInfo eventInfo4;

        eventInfoMerge.merge(eventInfo1 = new EventInfo(new Event(new EventHeaderV4(), new QueryEventDataBuild().setSql("BEGIN").build())));
        eventInfoMerge.merge(eventInfo2 = new EventInfo(new Event(new EventHeaderV4(), new TableMapEventDataBuild().build())));
        eventInfoMerge.merge(eventInfo3 = new EventInfo(new Event(new EventHeaderV4(), new WriteRowsEventDataBuild().build())));
        eventInfoMerge.merge(eventInfo4 = new EventInfo(new Event(new EventHeaderV4(), new XidEventDataBuild().build())));


        assertEquals(eventInfoMerge.size(), 4);
        assertEquals(eventInfoMerge.canMerge(), false);


        iterator = eventInfoMerge.parse().iterator();
        next = iterator.next();
        assertEquals(next.getEventInfo(), eventInfo1);
        assertEquals(next.getStepCount(), 4);
        assertEquals(next.getCurrentStep(), 1);
        assertEquals(next.isUseful(), false);
        assertEquals(next.isFirstStep(), true);
        assertEquals(next.isLastStep(), false);

        next = iterator.next();
        assertEquals(next.getEventInfo(), eventInfo2);
        assertEquals(next.getStepCount(), 4);
        assertEquals(next.getCurrentStep(), 2);
        assertEquals(next.isUseful(), false);
        assertEquals(next.isFirstStep(), false);
        assertEquals(next.isLastStep(), false);

        next = iterator.next();
        assertEquals(next.getEventInfo(), eventInfo3);
        assertEquals(next.getStepCount(), 4);
        assertEquals(next.getCurrentStep(), 3);
        assertEquals(next.isUseful(), true);
        assertEquals(next.isFirstStep(), false);
        assertEquals(next.isLastStep(), false);

        next = iterator.next();
        assertEquals(next.getEventInfo(), eventInfo4);
        assertEquals(next.getStepCount(), 4);
        assertEquals(next.getCurrentStep(), 4);
        assertEquals(next.isUseful(), false);
        assertEquals(next.isFirstStep(), false);
        assertEquals(next.isLastStep(), true);




        reset();

        EventInfo eventInfo5;
        EventInfo eventInfo6;
        EventInfo eventInfo7;
        EventInfo eventInfo8;

        eventInfoMerge.merge(eventInfo1 = new EventInfo(new Event(new EventHeaderV4(), new QueryEventDataBuild().setSql("BEGIN").build())));
        eventInfoMerge.merge(eventInfo2 = new EventInfo(new Event(new EventHeaderV4(), new TableMapEventDataBuild().build())));
        eventInfoMerge.merge(eventInfo3 = new EventInfo(new Event(new EventHeaderV4(), new WriteRowsEventDataBuild().build())));
        eventInfoMerge.merge(eventInfo4 = new EventInfo(new Event(new EventHeaderV4(), new TableMapEventDataBuild().build())));
        eventInfoMerge.merge(eventInfo5 = new EventInfo(new Event(new EventHeaderV4(), new WriteRowsEventDataBuild().build())));
        eventInfoMerge.merge(eventInfo6 = new EventInfo(new Event(new EventHeaderV4(), new TableMapEventDataBuild().build())));
        eventInfoMerge.merge(eventInfo7 = new EventInfo(new Event(new EventHeaderV4(), new DeleteRowsEventDataBuild().build())));
        eventInfoMerge.merge(eventInfo8 = new EventInfo(new Event(new EventHeaderV4(), new QueryEventDataBuild().setSql(BaseConst.COMMIT).build())));


        assertEquals(eventInfoMerge.size(), 8);
        assertEquals(eventInfoMerge.canMerge(), false);


        iterator = eventInfoMerge.parse().iterator();
        next = iterator.next();
        assertEquals(next.getEventInfo(), eventInfo1);
        assertEquals(next.getStepCount(), 8);
        assertEquals(next.getCurrentStep(), 1);
        assertEquals(next.isUseful(), false);
        assertEquals(next.isFirstStep(), true);
        assertEquals(next.isLastStep(), false);

        next = iterator.next();
        assertEquals(next.getEventInfo(), eventInfo2);
        assertEquals(next.getStepCount(), 8);
        assertEquals(next.getCurrentStep(), 2);
        assertEquals(next.isUseful(), false);
        assertEquals(next.isFirstStep(), false);
        assertEquals(next.isLastStep(), false);

        next = iterator.next();
        assertEquals(next.getEventInfo(), eventInfo3);
        assertEquals(next.getStepCount(), 8);
        assertEquals(next.getCurrentStep(), 3);
        assertEquals(next.isUseful(), true);
        assertEquals(eventInfo3.getSqlType(), SqlType.DML.INSERT);
        assertEquals(next.isFirstStep(), false);
        assertEquals(next.isLastStep(), false);

        next = iterator.next();
        assertEquals(next.getEventInfo(), eventInfo4);
        assertEquals(next.getStepCount(), 8);
        assertEquals(next.getCurrentStep(), 4);
        assertEquals(next.isUseful(), false);
        assertEquals(next.isFirstStep(), false);
        assertEquals(next.isLastStep(), false);

        next = iterator.next();
        assertEquals(next.getEventInfo(), eventInfo5);
        assertEquals(next.getStepCount(), 8);
        assertEquals(next.getCurrentStep(), 5);
        assertEquals(next.isUseful(), true);
        assertEquals(eventInfo5.getSqlType(), SqlType.DML.INSERT);
        assertEquals(next.isFirstStep(), false);
        assertEquals(next.isLastStep(), false);

        next = iterator.next();
        assertEquals(next.getEventInfo(), eventInfo6);
        assertEquals(next.getStepCount(), 8);
        assertEquals(next.getCurrentStep(), 6);
        assertEquals(next.isUseful(), false);
        assertEquals(next.isFirstStep(), false);
        assertEquals(next.isLastStep(), false);

        next = iterator.next();
        assertEquals(next.getEventInfo(), eventInfo7);
        assertEquals(next.getStepCount(), 8);
        assertEquals(next.getCurrentStep(), 7);
        assertEquals(next.isUseful(), true);
        assertEquals(eventInfo7.getSqlType(), SqlType.DML.DELETE);
        assertEquals(next.isFirstStep(), false);
        assertEquals(next.isLastStep(), false);

        next = iterator.next();
        assertEquals(next.getEventInfo(), eventInfo8);
        assertEquals(next.getStepCount(), 8);
        assertEquals(next.getCurrentStep(), 8);
        assertEquals(next.isUseful(), false);
        assertEquals(eventInfo8.getSqlType(), SqlType.DML.COMMIT);
        assertEquals(next.isFirstStep(), false);
        assertEquals(next.isLastStep(), true);
    }


    /**
     * 测试schema不一样的情况
     */
    @Test
    public void testInsertSchema() throws EventInfoCreateException, EventMergeException, IllegalAccessException {
        ExposeConfig config = new ExposeConfig();
        Field schemaField = ReflectionUtils.findField(ExposeConfig.class, "schema");
        ReflectionUtils.makeAccessible(schemaField);
        schemaField.set(config,"t_move");

        eventInfoMerge = new BaseEventInfoMerge(config);

        eventInfoMerge.merge(new EventInfo(new Event(new EventHeaderV4Build().build(),new QueryEventDataBuild().setDatabase("t_move1").setSql("BEGIN").build())));
        eventInfoMerge.merge(new EventInfo(new Event(new EventHeaderV4Build().build(),new TableMapEventDataBuild().setDatabase("t_move1").setTable("t1").build())));
        eventInfoMerge.merge(new EventInfo(new Event(new EventHeaderV4Build().build(),new DeleteRowsEventDataBuild().build())));
        eventInfoMerge.merge(new EventInfo(new Event(new EventHeaderV4Build().build(),new XidEventDataBuild().build())));


        assertEquals(eventInfoMerge.size(),0);
        assertEquals(eventInfoMerge.canMerge(),true);


        eventInfoMerge.merge(new EventInfo(new Event(new EventHeaderV4Build().build(),new QueryEventDataBuild().setDatabase("t_move").setSql("BEGIN").build())));
        eventInfoMerge.merge(new EventInfo(new Event(new EventHeaderV4Build().build(),new TableMapEventDataBuild().setDatabase("t_move").setTable("t1").build())));
        eventInfoMerge.merge(new EventInfo(new Event(new EventHeaderV4Build().build(),new DeleteRowsEventDataBuild().build())));
        eventInfoMerge.merge(new EventInfo(new Event(new EventHeaderV4Build().build(),new XidEventDataBuild().build())));

    }

}
