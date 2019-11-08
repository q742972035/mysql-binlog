package build;

import com.github.shyiko.mysql.binlog.event.EventHeaderV4;
import com.github.shyiko.mysql.binlog.event.EventType;

/**
 * @program: mysql-binlog-incr-expose
 * @description:
 * @author: 张忆
 * @create: 2019-08-02 21:11
 **/
public class EventHeaderV4Build {
    private EventHeaderV4 eventHeaderV4 = new EventHeaderV4();

    public EventHeaderV4Build setTimestamp(long ts) {
        eventHeaderV4.setTimestamp(ts);
        return this;
    }

    public EventHeaderV4Build setEventType(EventType et) {
        eventHeaderV4.setEventType(et);
        return this;
    }

    public EventHeaderV4Build setServerId(long id) {
        eventHeaderV4.setServerId(id);
        return this;
    }

    public EventHeaderV4Build setEventLength(long length) {
        eventHeaderV4.setEventLength(length);
        return this;
    }

    public EventHeaderV4Build setNextPosition(long nextPosition) {
        eventHeaderV4.setNextPosition(nextPosition);
        return this;
    }

    public EventHeaderV4Build setFlags(int flags) {
        eventHeaderV4.setFlags(flags);
        return this;
    }

    public EventHeaderV4 build() {
        return eventHeaderV4;
    }
}
