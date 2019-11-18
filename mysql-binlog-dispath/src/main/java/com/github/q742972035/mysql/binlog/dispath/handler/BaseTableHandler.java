package com.github.q742972035.mysql.binlog.dispath.handler;


import com.github.q742972035.mysql.binlog.dispath.annotation.dml.*;
import com.github.q742972035.mysql.binlog.dispath.exception.GenericClassNotFoundException;
import com.github.q742972035.mysql.binlog.expose.global.Global;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

/**
 * this is a base talbe handler for better use
 * each handlerAction has owner thread-pool(single thread)
 */
public abstract class BaseTableHandler<T> {

    /**
     * The generic class name
     */
    public final String tClassName;

    public BaseTableHandler() throws GenericClassNotFoundException {
        Type type = getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType){
            ParameterizedType parameterizedType = (ParameterizedType) type;
            tClassName = parameterizedType.getActualTypeArguments()[0].getTypeName();
        }else {
            throw new GenericClassNotFoundException();
        }
    }

    private volatile List<HandlerAction> handlerActions;

    private volatile boolean updating = true;

    private void updated(){
        this.updating = false;
    }


    /**
     * initialization operation for correcting the data
     */
    protected void setHandlerActions(List<HandlerAction> handlerActions) {
        if (this.handlerActions == null) {
            synchronized (this) {
                if (this.handlerActions == null) {
                    this.handlerActions = handlerActions;
                    // 初始化操作
                    for (HandlerAction handlerAction : this.handlerActions) {
                        handlerAction.init();
                    }
                    updated();
                }
            }
        }
    }

    public List<HandlerAction> getHandlerActions() {
        return handlerActions;
    }

    @Insert
    public void insert(T t) throws InterruptedException, NoSuchMethodException {
        while (updating) {
            Thread.sleep(500);
        }
        handlerAction(HandlerAction.Type.insert,t);
    }

    @Update
    public void update(@Before T before, @After T after) throws InterruptedException, NoSuchMethodException {
        while (updating) {
            Thread.sleep(500);
        }
        handlerAction(HandlerAction.Type.update,Arrays.asList(before,after));
    }


    @Delete
    public void delete(T t) throws InterruptedException, NoSuchMethodException {
        while (updating) {
            Thread.sleep(500);
        }
        handlerAction(HandlerAction.Type.delete,t);
    }

    private void handlerAction(HandlerAction.Type type,Object object) throws NoSuchMethodException {
        if (handlerActions==null){
            throw new IllegalStateException("handlerActions is null, you should call method "+BaseTableHandler.class.getMethod("setHandlerActions", List.class).getName());
        }
        for (HandlerAction handlerAction : handlerActions) {
            handlerAction.setType(type);
            handlerAction.setObject(object);
            handlerAction.setDataBase(Global.CURRENT_DB.get());
            handlerAction.setTable(Global.CURRENT_TB.get());
            handlerAction.setCurrentPosition(Global.CURRENT_POSITION.get());
            handlerAction.setNextPosition(Global.NEXT_POSITION.get());
            // execute by a single thread(pass a FIFO queue)
            handlerAction.getOneExecutors().execute(handlerAction);
        }
    }
}
