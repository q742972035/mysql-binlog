package type;

import build.*;
import com.github.q742972035.mysql.binlog.incr.expose.build.EventInfo;
import com.github.q742972035.mysql.binlog.incr.expose.exception.EventInfoCreateException;
import com.github.q742972035.mysql.binlog.incr.expose.type.DDLNextPositionCanUseTypeMatch;
import com.github.q742972035.mysql.binlog.incr.expose.type.DMLNextPositionCanUseTypeMatch;
import com.github.q742972035.mysql.binlog.incr.expose.type.NextPositionCanUseTypeMatch;
import com.github.shyiko.mysql.binlog.event.*;
import com.github.shyiko.mysql.binlog.event.deserialization.ChecksumType;
import org.junit.Test;
import print.PrintUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @program: mysql-binlog-incr-expose
 * @description:
 * @author: 张忆
 * @create: 2019-08-02 19:45
 **/
public class NextPositionCanUseTypeMatchTest {


    public <T extends NextPositionCanUseTypeMatch> NextPositionCanUseTypeMatch create(Class<T> clazz) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<T> constructor = clazz.getConstructor();
        if (!constructor.isAccessible()) {
            constructor.setAccessible(true);
        }
        return constructor.newInstance();
    }


    @Test
    public void testddl() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, EventInfoCreateException {
        NextPositionCanUseTypeMatch ddlNextPositionCanUseTypeMatch = create(DDLNextPositionCanUseTypeMatch.class);
        EventInfo eventInfo;
        // ROTATE的nextPosition没用
        Event event = new Event(
                new EventHeaderV4Build().setTimestamp(0).setEventType(EventType.ROTATE).setServerId(1).setEventLength(19 + 28).setNextPosition(0).setFlags(32).build(),
                new RotateEventDataBuild().setBinlogFilename("mysql-bin.000002").setBinlogPosition(120L).build()
        );
        boolean match = ddlNextPositionCanUseTypeMatch.match(eventInfo = new EventInfo(event));
        System.out.println(match); // fasle
        if (!match) {
            PrintUtil.pln("match:%s nextposition:%s", match, eventInfo.getNextPosition());
        }

        // FORMAT_DESCRIPTION的nextPosition没用
        event = new Event(
                new EventHeaderV4Build().setTimestamp(1564744602000L).setEventType(EventType.FORMAT_DESCRIPTION).setServerId(1).setEventLength(19 + 97)
                        .setNextPosition(0).setFlags(0).build(),
                new FormatDescriptionEventDataBuild().setChecksumType(ChecksumType.CRC32)
                        .setDataLength(92)
                        .setBinlogVersion(4)
                        .setHeaderLength(97)
                        .setServerVersion("5.6.44-log")
                        .build()
        );
        match = ddlNextPositionCanUseTypeMatch.match(eventInfo = new EventInfo(event));
        System.out.println(match); // fasle
        if (!match) {
            PrintUtil.pln("match:%s nextposition:%s", match, eventInfo.getNextPosition());
        }

        // QUERY的nextPosition有用
        event = new Event(
                new EventHeaderV4Build().setTimestamp(1564744602000L).setEventType(EventType.QUERY).setServerId(1).setEventLength(19 + 97)
                        .setNextPosition(299).setFlags(0).build(),
                new QueryEventDataBuild().setErrorCode(0)
                        .setThreadId(92)
                        .setExecutionTime(4)
                        .setDatabase("t_move")
                        .setSql("CREATE TABLE `InnoDB_create` (\n" +
                                "`id`  int NULL AUTO_INCREMENT ,\n" +
                                "PRIMARY KEY (`id`)\n" +
                                ")\n" +
                                "ENGINE=InnoDB")
                        .build()
        );
        match = ddlNextPositionCanUseTypeMatch.match(eventInfo = new EventInfo(event));
        System.out.println(match); // true
        if (match) {
            PrintUtil.pln("match:%s nextposition:%s", match, eventInfo.getNextPosition());
        }

        event = new Event(
                new EventHeaderV4Build().setTimestamp(1564744602000L).setEventType(EventType.QUERY).setServerId(1).setEventLength(19 + 97)
                        .setNextPosition(1148).setFlags(0).build(),
                new QueryEventDataBuild().setErrorCode(0)
                        .setThreadId(92)
                        .setExecutionTime(4)
                        .setDatabase("t_move")
                        .setSql("ALTER TABLE `InnoDB_create`\n" +
                                "ADD COLUMN `aa`  varchar(22) NULL AFTER `id`")
                        .build()
        );

        match = ddlNextPositionCanUseTypeMatch.match(eventInfo = new EventInfo(event));
        System.out.println(match); // true

        if (match) {
            PrintUtil.pln("match:%s nextposition:%s", match, eventInfo.getNextPosition());
        }
    }

    @Test
    public void testdml() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, EventInfoCreateException {
        NextPositionCanUseTypeMatch dml = create(DMLNextPositionCanUseTypeMatch.class);
        EventInfo eventInfo;

        QueryEventData falseData = new QueryEventDataBuild()
                .setErrorCode(0)
                .setThreadId(78)
                .setExecutionTime(0)
                .setDatabase("t_move")
                .setSql("BEGIN").build();

        QueryEventData trueData = new QueryEventDataBuild()
                .setErrorCode(0)
                .setThreadId(78)
                .setExecutionTime(0)
                .setDatabase("t_move")
                .setSql("COMMIT").build();

        XidEventData trueData1 = new XidEventDataBuild()
                .setXid(443L).build();

        EventHeaderV4 headerV4 = new EventHeaderV4Build().setTimestamp(0).setEventType(EventType.ROTATE).setServerId(1).setEventLength(19 + 28).setNextPosition(99).setFlags(32).build();

        boolean match;

        match = dml.match(eventInfo = new EventInfo(new Event(headerV4, falseData)));
        System.out.println(match);//false

        match = dml.match(eventInfo = new EventInfo(new Event(headerV4, trueData)));
        System.out.println(match);//true
        match = dml.match(eventInfo = new EventInfo(new Event(headerV4, trueData1)));
        System.out.println(match);//true


        // xid 总是true
        match = dml.match(eventInfo = new EventInfo(new Event(headerV4, new XidEventData())));
        System.out.println(match);
    }

}
