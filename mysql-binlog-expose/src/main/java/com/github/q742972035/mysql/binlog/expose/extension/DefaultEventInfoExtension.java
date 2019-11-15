package com.github.q742972035.mysql.binlog.expose.extension;


import com.github.q742972035.mysql.binlog.expose.build.BaseEventInfoMerge;
import com.github.q742972035.mysql.binlog.expose.build.BaseEventInfoMerge;

/**
 * @program: mysql-binlog-incr-expose
 * @description
 * @author: zy
 * @create: 2019-08-09 08:52
 **/
public class DefaultEventInfoExtension extends AbstractEventInfoExtension {

    private BaseEventInfoMerge eventInfoMerge;

    @Override
    public void setStepCount(int stepCount) {
        this.stepCount = stepCount;
    }

    @Override
    public void setCurrentStep(int currentStep) {
        this.currentStep = currentStep;
    }

    @Override
    public void setLastStep(boolean lastStep) {
        this.lastStep = lastStep;
    }

    @Override
    public void setFirstStep(boolean firstStep) {
        this.firstStep = firstStep;
    }

    public void setEventInfoMerge(BaseEventInfoMerge eventInfoMerge) {
        this.eventInfoMerge = eventInfoMerge;
    }

    @Override
    public String toString() {
        return "DefaultEventInfoExtension{" +
                "stepCount=" + stepCount +
                ", currentStep=" + currentStep +
                ", lastStep=" + lastStep +
                ", firstStep=" + firstStep +
                ", eventInfo=" + eventInfo +
                '}';
    }
}
