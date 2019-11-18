package com.github.q742972035.mysql.binlog.dispath.handler;

import com.github.q742972035.mysql.binlog.expose.global.Global;
import com.github.q742972035.tools.ThreadFactoryBuild;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

public abstract class BaseHandlerAction<T> implements HandlerAction {

    private BlockingQueue<Object> objectBlockingQueue = new LinkedBlockingDeque<>();
    private BlockingQueue<Type> typeBlockingQueue = new LinkedBlockingDeque<>();

    private BlockingQueue<String> dbQueue = new LinkedBlockingDeque<>();
    private BlockingQueue<String> tbQueue = new LinkedBlockingDeque<>();
    private BlockingQueue<Long> cpQueue = new LinkedBlockingDeque<>();
    private BlockingQueue<Long> npQueue = new LinkedBlockingDeque<>();

    ExecutorService executor = Executors.newSingleThreadExecutor(ThreadFactoryBuild.build("executor-"+getClass().getSimpleName()));

    @Override
    public ExecutorService getOneExecutors() {
        return executor;
    }

    @Override
    public void setType(Type type) {
        try {
            typeBlockingQueue.put(type);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setObject(Object object) {
        try {
            objectBlockingQueue.put(object);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Type getType() {
        try {
            return typeBlockingQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Object getObj() {
        try {
            return objectBlockingQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public void setDataBase(String dataBase) {
        try {
            dbQueue.put(dataBase);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setTable(String table) {
        try {
            tbQueue.put(table);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setCurrentPosition(Long currentPosition) {
        try {
            cpQueue.put(currentPosition);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setNextPosition(Long nextPosition) {
        try {
            npQueue.put(nextPosition);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        Object obj = getObj();
        initGlobal();
        try {
            switch (getType()) {
                case insert:
                    doInsert((T) obj);
                    break;
                case update:
                    List<T> list = (List<T>) obj;
                    doUpdate(list.get(0), list.get(1));
                    break;
                case delete:
                    doDelete((T) obj);
                    break;
                default:
                    break;
            }
        } finally {
            removeGlobal();
        }
    }

    private void removeGlobal() {
        Global.CURRENT_TB.remove();
        Global.CURRENT_DB.remove();
        Global.CURRENT_POSITION.remove();
        Global.NEXT_POSITION.remove();
    }

    private void initGlobal() {
        try {
            Global.CURRENT_TB.set(tbQueue.take());
            Global.CURRENT_DB.set(dbQueue.take());
            Global.CURRENT_POSITION.set(cpQueue.take());
            Global.NEXT_POSITION.set(npQueue.take());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public abstract void doInsert(T t);

    public abstract void doUpdate(T before, T after);

    public abstract void doDelete(T t);
}
