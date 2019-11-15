package print;

/**
 * @program: mysql-binlog-incr-expose
 * @description
 * @author: zy
 * @create: 2019-08-02 16:10
 **/
public class PrintUtil {
    private static final String SEPARATOR = System.getProperty("line.separator");

    public static void pln(String format, Object... args) {
        format = format.replace(" ",SEPARATOR);
        System.out.println(String.format(format, args));
    }
}
