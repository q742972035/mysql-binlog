package print;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: mysql-binlog-incr-expose
 * @description:
 * @author: 张忆
 * @create: 2019-08-02 23:19
 **/
public class PrintAppend {
    private StringBuilder format = new StringBuilder();
    private List<Object> list = new ArrayList<>();

    private int firstTimes;
    private int secondTimes;


    public PrintAppend setFirst(String str) {
        firstTimes++;
        format.append(str);
        return this;
    }

    public PrintAppend setSecond(Object o) {
        secondTimes++;
        list.add(o);
        return this;
    }

    public void pln() {
        if (firstTimes != secondTimes) {
            throw new IllegalStateException(String.format("firstTimes[%s] 必须与 secondTimes[%s]的值相等", firstTimes, secondTimes));
        }
        PrintUtil.pln(format.toString(), list.toArray(new Object[0]));
    }

}
