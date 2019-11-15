package reactor.obj;

/**
 * @program: mysql-binlog-incr-expose
 * @description
 * @author: zy
 * @create: 2019-08-08 17:05
 **/
public class SimpleBoolean implements BooleanAcquire{
    private boolean b1;
    private boolean b2;

    @Override
    public boolean getb1() {
        return b1;
    }

    @Override
    public boolean getb2() {
        return b2;
    }
}
