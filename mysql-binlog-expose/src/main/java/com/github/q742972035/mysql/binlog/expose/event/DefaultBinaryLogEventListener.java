package com.github.q742972035.mysql.binlog.expose.event;

import com.github.q742972035.mysql.binlog.expose.build.BaseEventInfoMerge;
import com.github.q742972035.mysql.binlog.expose.build.BinLogInfo;
import com.github.q742972035.mysql.binlog.expose.build.CommitDMLEventInfoMerge;
import com.github.q742972035.mysql.binlog.expose.build.ExposeConfig;
import com.github.q742972035.mysql.binlog.expose.build.*;
import com.github.q742972035.mysql.binlog.expose.build.factory.EventInfoMergeFacotry;
import com.github.q742972035.mysql.binlog.expose.exception.EventInfoCreateException;
import com.github.q742972035.mysql.binlog.expose.exception.EventMergeException;
import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.event.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: mysql-binlog-incr-expose
 * @description:
 * @author: 张忆
 * @create: 2019-08-09 19:01
 **/
public class DefaultBinaryLogEventListener implements BinaryLogClient.EventListener {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private BaseEventInfoMerge eventInfoMerge;
    private ExposeConfig exposeConfig;
    /**
     * 处理eventInfo的其他信息，如错误处理等
     */
    private EventInfoExtHandler eventInfoExtHandler;

    public DefaultBinaryLogEventListener(ExposeConfig exposeConfig) {
        this.exposeConfig = exposeConfig;

        eventInfoExtHandler = new EventInfoExtHandler(exposeConfig);
        ConnectionEventListener connectionEventListener = new ConnectionEventListener() {
            @Override
            public void onConnect(boolean connected, BinLogInfo binLogInfo) {
                if (connected) {
                    currentPosition = binLogInfo.getBinlogPosition();
                }
                exposeConfig.removeConnectionEvent(this);
            }
        };
        exposeConfig.getConnectionEvents().add(connectionEventListener);
    }

    private long currentPosition = -100L;

    @Override
    public void onEvent(Event event) {
        EventInfo eventInfo;
        try {
            eventInfo = new EventInfo(event);
        } catch (EventInfoCreateException e) {
            logger.error("", e);
            return;
        }
        eventInfoExtHandler.handlerIfHappenErrorCode(eventInfoMerge, eventInfo);

        if (eventInfoMerge == null) {
            eventInfoMerge = EventInfoMergeFacotry.createEventInfoMerge(eventInfo, exposeConfig);
        } else {
            // 装饰eventInfoMerge，进一步更细节的包装他
            eventInfoMerge = EventInfoMergeFacotry.coudleBeNewEventInfoMerge(eventInfoMerge, eventInfo);
        }

        try {
            eventInfoMerge.merge(eventInfo);
        } catch (EventMergeException e) {
            logger.error("", e);
            eventInfoMerge = null;
            return;
        }

        if (eventInfoMerge.size() == 0) {
            logger.warn(String.format("发生非法的合并，eventInfo:%s", eventInfo.toString()));
            eventInfoMerge = null;
            return;
        }

        if (!eventInfoMerge.canMerge()) {
            try {
                eventInfoMerge.setCurrentPosition(currentPosition);
                if (eventInfoMerge instanceof XidDMLEventInfoMerge) {
                    push((XidDMLEventInfoMerge) eventInfoMerge);
                } else if (eventInfoMerge instanceof CommitDMLEventInfoMerge) {
                    push((CommitDMLEventInfoMerge) eventInfoMerge);
                } else if (eventInfoMerge instanceof DDLEventInfoMerge) {
                    push((DDLEventInfoMerge) eventInfoMerge);
                } else {
                    push(eventInfoMerge);
                }
            } finally {
                if (eventInfoMerge.getNextPosition() != 0L) {
                    currentPosition = eventInfoMerge.getNextPosition();
                }
                eventInfoMerge = null;
            }
        }
    }

    private void push(CommitDMLEventInfoMerge merge) {
        for (EventInfoMergeListener eventInfoMergeListener : exposeConfig.getEventInfoMergeListeners()) {
            eventInfoMergeListener.onEvent(merge);
            eventInfoMergeListener.onEvent((DMLEventInfoMerge) merge);
        }
    }

    private void push(XidDMLEventInfoMerge merge) {
        for (EventInfoMergeListener eventInfoMergeListener : exposeConfig.getEventInfoMergeListeners()) {
            eventInfoMergeListener.onEvent(merge);
            eventInfoMergeListener.onEvent((DMLEventInfoMerge) merge);
        }
    }

    private void push(DDLEventInfoMerge merge) {
        for (EventInfoMergeListener eventInfoMergeListener : exposeConfig.getEventInfoMergeListeners()) {
            eventInfoMergeListener.onEvent(merge);
        }
    }

    private void push(BaseEventInfoMerge merge) {
        for (EventInfoMergeListener eventInfoMergeListener : exposeConfig.getEventInfoMergeListeners()) {
            eventInfoMergeListener.onEvent(merge);
        }
    }
}
