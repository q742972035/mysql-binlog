package build;

import com.github.shyiko.mysql.binlog.event.Event;
import com.github.shyiko.mysql.binlog.event.EventHeaderV4;
import com.github.shyiko.mysql.binlog.event.deserialization.ChecksumType;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @program: mysql-binlog-incr-expose
 * @description:
 * @author: 张忆
 * @create: 2019-08-09 19:04
 **/
public class AbstractTest {

    protected void setField(Class clazz,String name,Object src,Object value) throws NoSuchFieldException, IllegalAccessException {
        Field field = clazz.getDeclaredField(name);
        field.setAccessible(true);
        field.set(src,value);
    }

    protected Object invokeMethod(Class clazz,String name,Object src,Class[] parameters,Object[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method declaredMethod = clazz.getDeclaredMethod(name, parameters);
        declaredMethod.setAccessible(true);
        Object invoke = declaredMethod.invoke(src, args);
        return invoke;
    }

    protected Object getField(Class clazz,String name,Object src) throws IllegalAccessException, NoSuchFieldException {
        Field field = clazz.getDeclaredField(name);
        field.setAccessible(true);
        return field.get(src);
    }


    protected Event createRotate(){
        return new Event(new EventHeaderV4Build().build(),new RotateEventDataBuild().setBinlogFilename("mysql-bin.000002").setBinlogPosition(120).build());
    }
    protected Event createFormatDescription(){
        return new Event(new EventHeaderV4Build().build(),new FormatDescriptionEventDataBuild().setChecksumType(ChecksumType.CRC32).setBinlogVersion(120).setServerVersion("5.6.44-log").build());
    }
    protected Event createQuery(String ddl,int nextPosition){
        return new Event(new EventHeaderV4Build().setNextPosition(nextPosition).build(),new QueryEventDataBuild().setSql(ddl).build());
    }

    protected Event createTableMap(int nextPosition){
        return new Event(new EventHeaderV4Build().setNextPosition(nextPosition).build(),new TableMapEventDataBuild().build());
    }
    protected Event createWriteRows(int nextPosition){
        return new Event(new EventHeaderV4Build().setNextPosition(nextPosition).build(),new WriteRowsEventDataBuild().build());
    }
    protected Event createXID(int nextPosition){
        return new Event(new EventHeaderV4Build().setNextPosition(nextPosition).build(),new XidEventDataBuild().build());
    }
    protected Event createUpdateRows(int nextPosition){
        return new Event(new EventHeaderV4Build().setNextPosition(nextPosition).build(),new UpdateRowsEventDataBuild().build());
    }
    protected Event createDeleteRows(int nextPosition){
        return new Event(new EventHeaderV4Build().setNextPosition(nextPosition).build(),new DeleteRowsEventDataBuild().build());
    }
}
