package com.github.q742972035.mysql.binlog.dispath;

import com.github.q742972035.mysql.binlog.dispath.handler.BaseTableHandler;
import com.github.q742972035.mysql.binlog.dispath.scan.MethodMetadata;
import com.github.q742972035.mysql.binlog.dispath.scan.MethodParameter;
import com.github.q742972035.mysql.binlog.dispath.scan.ObjectBind;
import com.github.q742972035.mysql.binlog.expose.build.TableElement;
import com.github.q742972035.mysql.binlog.expose.global.Global;
import com.github.q742972035.mysql.binlog.expose.utils.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.List;

public class RunnerDispath {
    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 方法所在的实例
     */
    Object methodTarget;

    /**
     * 方法元数据
     */
    MethodMetadata methodMetadata;

    List<TableElement> tableElements;
    long currentPosition;
    long nextPosition;

    public RunnerDispath(Object methodTarget, MethodMetadata methodMetadata, List<TableElement> tableElements, long currentPosition, long nextPosition) {
        this.methodTarget = methodTarget;
        this.methodMetadata = methodMetadata;
        this.tableElements = tableElements;
        this.currentPosition = currentPosition;
        this.nextPosition = nextPosition;
    }

    public void dispath() throws Exception {
        if (methodMetadata.getMethodParameters().size() == 0) {
            methodMetadata.getMethod().invoke(this.methodTarget);
        } else {
            try {
                preHandler();
                Global.CURRENT_POSITION.set(currentPosition);
                Global.NEXT_POSITION.set(nextPosition);
                methodMetadata.getMethod().invoke(this.methodTarget, ObjectBind.bind(methodMetadata, tableElements, currentPosition, nextPosition));
                if (logger.isDebugEnabled()) {
                    logger.debug("current db is {},current table is {} currentPositon is {},nextPosition is {}，invoke is {}",
                            new Object[]{Global.CURRENT_DB.get(), Global.CURRENT_TB.get(), currentPosition, nextPosition});
                }
                //help gc...
            } catch (Exception e) {
                if (logger.isErrorEnabled()) {
                    logger.error(String.format("类[ %s ]方法[ %s ]出现异常,currentPosition is [%s],nextPosition is [%s]",
                            methodTarget.getClass().getName(), methodMetadata.getName(), currentPosition, nextPosition), e);
                }
            }
        }
    }

    private void preHandler() throws IllegalAccessException, ClassNotFoundException {
        // handler special type because the correct type of Object[class] was not obtained
        if (BaseTableHandler.class.getName().equals(methodMetadata.getMethod().getDeclaringClass().getName())) {
            List<MethodParameter> methodParameters = methodMetadata.getMethodParameters();
            for (MethodParameter methodParameter : methodParameters) {
                Class clazz = methodParameter.getClazz();
                if (clazz == Object.class) {
                    // set the class to exact class instead of Object class
                    Field tClassNameField = ReflectionUtils.findField(BaseTableHandler.class, "tClassName");
                    String genericClassName = (String) tClassNameField.get(this.methodTarget);

                    Field clazzField = ReflectionUtils.findField(MethodParameter.class, "clazz");
                    ReflectionUtils.makeAccessible(clazzField);
                    clazzField.set(methodParameter, Class.forName(genericClassName));
                }
            }
        }
    }
}
