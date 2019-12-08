package com.github.q742972035.mysql.binlog.dispatch.handler;

import java.util.concurrent.ExecutorService;

public interface HandlerAction extends Runnable{

    void init();


    ExecutorService getOneExecutors();

    enum Type{
        insert,update,delete
    }

    void setType(Type type);
    void setObject(Object object);

    Type getType();
    Object getObj();

    void setDataBase(String dataBase);

    void setTable(String table);

    void setCurrentPosition(Long currentPosition);

    void setNextPosition(Long nextPosition);
}
