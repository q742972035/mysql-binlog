package com.github.q742972035.mysql.binlog.dispatch.list;

import org.junit.Before;
import org.junit.Test;

import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.*;

public class ReadWriteLinkedListTest {

    ThreadFactory threadFactory;

    String namePrefix = "测试线程:";

    @Before
    public void setup() {
        threadFactory = new ThreadFactory() {
            private AtomicInteger integer = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, namePrefix + integer.getAndIncrement());
            }
        };
    }


    @Test
    public void testTwoThreadReadWrite() {
        ReadWriteLinkedList<Integer> list = new ReadWriteLinkedList<>();
        list.setRunBack(integer -> {
            String msg = integer + "-------" + Thread.currentThread().getName();
//            assertThat(msg).isEqualTo(integer+"-------"+namePrefix+integer);
            System.out.println(msg);
        });

        ThreadPoolExecutor executor = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 0, TimeUnit.MICROSECONDS, new ArrayBlockingQueue<>(16), threadFactory);
        int i = 0;
        while (true) {
            try {
                Thread.sleep(50);
                list.addAndlooping(i++, executor);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    @Test
    public void testTwoThreadReadWrite1() throws InterruptedException {
        int linkedListsLen = 20;
        ReadWriteLinkedList<String>[] linkedLists = new ReadWriteLinkedList[linkedListsLen];
        for (int i = 0; i < linkedLists.length; i++) {
            linkedLists[i] = new ReadWriteLinkedList<>();
            linkedLists[i].setRunBack(new ReadWriteLinkedList.RunBack<String>() {
                @Override
                public void result(String s) {
                    try {
                        Thread.sleep(3 + new Random().nextInt(50));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    String msg = s + Thread.currentThread().getName();
                    System.out.println(msg);
                }
            });
        }
        /**
         * 并行執行
         */
        ThreadPoolExecutor executor = new ThreadPoolExecutor(10, Integer.MAX_VALUE, 0, TimeUnit.MICROSECONDS, new SynchronousQueue<>(), threadFactory);
        /**
         * 串行执行
         * 原因：
         * 1. 当前线程数<核心线程数时，创建线程执行任务。
         * 2. 当前线程数>=核心线程数时，把新的任务放入阻塞队列。
         * 3. 当queue已满，并且最大线程数 > 核心线程数，创建线程执行任务。
         * 4. 当queue已满，并且最大线程数>=核心线程数，默认采取拒绝策略(RejectedExecutionHandler)。
         */
//        ThreadPoolExecutor executor = new ThreadPoolExecutor(1, Integer.MAX_VALUE, 0, TimeUnit.MICROSECONDS, new ArrayBlockingQueue<>(16), threadFactory);

        for (int i1 = 0; i1 < linkedLists.length; i1++) {
            int finalI = i1;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    int i = 1;
                    int remaind = new Random().nextInt(1000);
                    while (true) {
                        try {
                            Thread.sleep(new Random().nextInt(3));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        linkedLists[finalI].addAndlooping(build0(finalI + 1) + i, executor);
                        if (i++ % remaind == 0) {
                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            remaind = new Random().nextInt(1000);
                        }
                    }
                }
            }).start();
        }

        synchronized (this) {
            wait();
        }
    }

    @Test
    public void testSize() throws InterruptedException {
        ReadWriteLinkedList<String> linkedList = new ReadWriteLinkedList<>();

        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
        CountDownLatch latch = new CountDownLatch(5);
        linkedList.setRunBack(s ->{
            try {
                if (new Random().nextBoolean()){
                    throw new Exception("故意出错！");
                }
                System.out.println(linkedList.remainTaskCount());
            }finally {
                latch.countDown();
            }

        });

        linkedList.add("111");
        linkedList.add("111");
        linkedList.add("111");
        linkedList.add("111");
        linkedList.addAndlooping("222",executor);

        latch.await();
        Thread.sleep(100);
        assertThat(linkedList.remainTaskCount()).isEqualTo(0);
    }


    private static String build0(int size) {
        StringBuilder builder = new StringBuilder("0");
        for (int i = 0; i < size - 1; i++) {
            builder.append("0");
            if ((i + 1) % 3 == 0) {
                builder.append("-");
            }
        }
        return builder.toString();
    }
}