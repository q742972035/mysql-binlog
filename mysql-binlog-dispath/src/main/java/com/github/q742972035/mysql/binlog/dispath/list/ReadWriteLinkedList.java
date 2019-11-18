package com.github.q742972035.mysql.binlog.dispath.list;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 读写操作的linkedList
 *
 * @program: mysql-binlog-dispath
 * @description:
 * @author: 张忆
 * @create: 2019-10-01 21:57
 **/
public class ReadWriteLinkedList<T> implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ReadWriteLinkedList.class);
    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private Lock readLock = readWriteLock.readLock();
    private Lock writeLock = readWriteLock.writeLock();
    private AtomicReference<Thread> addThread = new AtomicReference<>();
    private RunBack<T> runBack;
    /**
     * 标记run方法是否执行
     */
    private AtomicBoolean isRunning = new AtomicBoolean(false);

    private LinkedList<T> linkedList;

    /**
     * 剩余任务数量
     */
    private AtomicLong remainTaskCount = new AtomicLong(0);


    public ReadWriteLinkedList() {
        linkedList = new LinkedList<>();
    }

    public ReadWriteLinkedList(Collection<T> collection) {
        linkedList = new LinkedList<>(collection);
    }

    /**
     * 确保add的时候只有同一个线程
     */
    public void add(T t) {
        if (!addThread.compareAndSet(null, Thread.currentThread())) {
            throw new IllegalStateException("ReadWriteLinkedList#add 方法最多只允许一个线程去操作");
        }
        readLock.lock();
        try {
            linkedList.addLast(t);
        } finally {
            readLock.unlock();
            addThread.set(null);
            remainTaskCount.incrementAndGet();
        }
    }

    private T poll() {
        writeLock.lock();
        try {
            return linkedList.pollFirst();
        } finally {
            writeLock.unlock();
        }
    }

    public void setRunBack(RunBack<T> runBack) {
        this.runBack = runBack;
    }

    /**
     * 执行add操作，并且启动的looping
     */
    public void addAndlooping(T t, ThreadPoolExecutor executor) {
        if (runBack == null) {
            throw new IllegalStateException("runBack cannot be null");
        }
        add(t);
        if (!isRunning.get()) {
            if (executor != null) {
                executor.execute(this);
            } else {
                new Thread(this).start();
            }
        }
    }

    /**
     * 获取剩余任务数量
     * @return
     */
    public long remainTaskCount() {
        return remainTaskCount.get();
    }

    @Override
    public void run() {
        if (!isRunning.compareAndSet(false, true)) {
            return;
        }
        try {
            T t;
            while ((t = poll()) != null) {
                try {
                    runBack.result(t);
                } catch (Exception e) {
                    if (logger.isErrorEnabled()) {
                        logger.error("", e);
                    }
                }finally {
                    remainTaskCount.decrementAndGet();
                }
            }
        } finally {
            isRunning.set(false);
        }
    }

    public static interface RunBack<T> {
        void result(T t) throws Exception;
    }
}
