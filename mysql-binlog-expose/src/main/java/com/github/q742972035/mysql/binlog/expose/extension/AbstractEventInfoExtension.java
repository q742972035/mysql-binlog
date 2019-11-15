package com.github.q742972035.mysql.binlog.expose.extension;

import com.github.q742972035.mysql.binlog.expose.type.sql.SqlType;
import com.github.q742972035.mysql.binlog.expose.build.EventInfo;
import com.github.q742972035.mysql.binlog.expose.type.sql.SqlType;

/**
 * @program: mysql-binlog-incr-expose
 * @description
 * @author: zy
 * @create: 2019-08-09 08:54
 **/
public abstract class AbstractEventInfoExtension implements EventInfoExtension {
    protected int stepCount;
    protected int currentStep;
    protected boolean lastStep;
    protected boolean firstStep;
    protected EventInfo eventInfo;
    private boolean useful;


    @Override
    public int getStepCount() {
        return stepCount;
    }

    @Override
    public int getCurrentStep() {
        return currentStep;
    }

    @Override
    public boolean isLastStep() {
        return lastStep;
    }

    @Override
    public boolean isFirstStep() {
        return firstStep;
    }

    @Override
    public void setWrap(EventInfo obj) {
        this.eventInfo = obj;
        SqlType sqlType = this.eventInfo.getSqlType();
        if (sqlType instanceof SqlType.DDL) {
            useful = true;
            return;
        }
        useful = sqlType.equals(SqlType.BaseInfo.FORMATD_ESCRIPTION)
                || sqlType.equals(SqlType.DML.INSERT)
                || sqlType.equals(SqlType.DML.UPDATE)
                || sqlType.equals(SqlType.DML.DELETE);
    }

    @Override
    public boolean isUseful() {
        return useful;
    }

    @Override
    public EventInfo getEventInfo() {
        return eventInfo;
    }

    public abstract void setStepCount(int stepCount);

    public abstract void setCurrentStep(int currentStep);

    public abstract void setLastStep(boolean lastStep);

    public abstract void setFirstStep(boolean firstStep);


}
