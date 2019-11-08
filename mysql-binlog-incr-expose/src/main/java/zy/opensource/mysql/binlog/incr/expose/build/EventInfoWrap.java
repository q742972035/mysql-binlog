package zy.opensource.mysql.binlog.incr.expose.build;

import zy.opensource.mysql.binlog.incr.expose.extension.DefaultEventInfoExtension;
import zy.opensource.mysql.binlog.incr.expose.extension.EventInfoExtension;

import java.util.Iterator;
import java.util.List;

/**
 * 包装eventInfo
 *
 * @program: mysql-binlog-incr-expose
 * @description
 * @author: zy
 * @create: 2019-08-08 09:13
 **/
public class EventInfoWrap implements Iterable<EventInfoExtension> {
    BaseEventInfoMerge eventInfoMerge;
    private int index = 0;
    private int size;


    public EventInfoWrap(BaseEventInfoMerge eventInfoMerge) {
        this.eventInfoMerge = eventInfoMerge;
        this.size = this.eventInfoMerge.size();
    }


    @Override
    public Iterator<EventInfoExtension> iterator() {
        return iterator;
    }

    private final EventInfoExtensionIterator iterator = new EventInfoExtensionIterator();

    private class EventInfoExtensionIterator implements Iterator<EventInfoExtension> {

        @Override
        public boolean hasNext() {
            return index < EventInfoWrap.this.eventInfoMerge.size();
        }

        @Override
        public EventInfoExtension next() {
            if (!hasNext()) {
                return null;
            }
            final List<EventInfo> eventInfos = EventInfoWrap.this.eventInfoMerge.eventInfos;
            DefaultEventInfoExtension extension = new DefaultEventInfoExtension();
            extension.setEventInfoMerge(EventInfoWrap.this.eventInfoMerge);
            extension.setWrap(eventInfos.get(EventInfoWrap.this.index++));
            extension.setStepCount(eventInfos.size());
            extension.setCurrentStep(EventInfoWrap.this.index);
            extension.setFirstStep(extension.getCurrentStep() == 1);
            extension.setLastStep(extension.getCurrentStep() == eventInfos.size());
            return extension;
        }
    }

}
