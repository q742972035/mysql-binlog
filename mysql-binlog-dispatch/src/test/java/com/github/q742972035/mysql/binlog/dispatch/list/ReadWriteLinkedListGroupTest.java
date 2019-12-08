package com.github.q742972035.mysql.binlog.dispatch.list;

import com.github.q742972035.mysql.binlog.dispatch.list.exception.NoSuchSubTaskException;
import com.github.q742972035.mysql.binlog.dispatch.list.exception.PrefixRepetitionException;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class ReadWriteLinkedListGroupTest {

    List<String> subTaskNames;
    /**
     * 定义业务处理时间
     */
    Map<String, RandomTime> randomTimeMap = new HashMap<>();

    /**
     * 时间片段
     */
    Map<Integer, String> subTaskNamesFragmentMap = new HashMap<>();

    Map<String, AtomicInteger> subTaskNameInteger = new HashMap<>();

    private void setFragment(int start, int end, String subTaskName) {
        for (int i = start; i < end; i++) {
            subTaskNamesFragmentMap.put(i, subTaskName);
        }
    }

    @Before
    public void setup() {
        subTaskNames = Arrays.asList("user", "bank", "user_home", "admin", "email", "shop", "address");
        randomTimeMap.put("user", new RandomTime(30, 80));
        randomTimeMap.put("bank", new RandomTime(10, 30));
        randomTimeMap.put("user_home", new RandomTime(50, 80));
        randomTimeMap.put("admin", new RandomTime(80, 120));
        randomTimeMap.put("email", new RandomTime(20, 50));
        randomTimeMap.put("shop", new RandomTime(10, 80));
        randomTimeMap.put("address", new RandomTime(20, 80));

        for (String subTaskName : subTaskNames) {
            subTaskNameInteger.put(subTaskName,new AtomicInteger());
        }

        setFragment(0, 15, "user");
        setFragment(15, 25, "bank");
        setFragment(25, 40, "user_home");
        setFragment(40, 50, "admin");
        setFragment(50, 70, "email");
        setFragment(70, 90, "shop");
        setFragment(90, 100, "address");
    }

    @Test
    public void simpleTest() throws PrefixRepetitionException, InterruptedException, NoSuchSubTaskException {
        int size = 10;
        String prefix = "prefix-";
        ReadWriteLinkedListGroup<Integer> group = new ReadWriteLinkedListGroup(size, prefix);
        List<String> allTaskName = group.getAllTaskName();
        AtomicInteger integer = new AtomicInteger();
        for (String taskName : allTaskName) {
            group.setRunBack(taskName, new ReadWriteLinkedList.RunBack<SubTaskBody<Integer>>() {
                @Override
                public void result(SubTaskBody<Integer> stringSubTaskBody) throws Exception {
                    RandomTime randomTime = randomTimeMap.get(stringSubTaskBody.getSubTaskName());
                    try {
                        long time = randomTime.getMaxDuration() - randomTime.getMinDuration();
                        Thread.sleep(randomTime.minDuration + new Random().nextInt((int) time));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    integer.getAndIncrement();
                    System.out.println(String.format("taskName:%s,subTaskName:%s,value:%s,Thread:%s",
                            taskName,stringSubTaskBody.getSubTaskName(),stringSubTaskBody.getTask(),Thread.currentThread().getName()));
                }
            });
        }


        new Thread(() -> {
            int k = 0;
            while (k++<500) {
                int random = ThreadLocalRandom.current().nextInt(100);
                String subTaskName = subTaskNamesFragmentMap.get(random);
                try {
                    group.excuseTask(new SubTaskBody<Integer>(subTaskName,subTaskNameInteger.get(subTaskName).getAndIncrement()));
                } catch (NoSuchSubTaskException e) {
                    e.printStackTrace();
                }
            }
        }).start();


        while (true){
            if (integer.get() == 500) {
                group.excuseTask(new SubTaskBody<Integer>("user",2222));
                group.excuseTask(new SubTaskBody<Integer>("address",4444));
                break;
            }
        }


        synchronized (this){
            wait();
        }
    }


    public static class RandomTime {
        long minDuration;
        long maxDuration;

        public RandomTime(long minDuration, long maxDuration) {
            this.minDuration = minDuration;
            this.maxDuration = maxDuration;
        }

        public long getMaxDuration() {
            return maxDuration;
        }

        public long getMinDuration() {
            return minDuration;
        }
    }

}