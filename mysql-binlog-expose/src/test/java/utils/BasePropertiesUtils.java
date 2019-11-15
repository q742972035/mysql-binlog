package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @program: mysql-binlog-incr-expose
 * @description
 * @author: zy
 * @create: 2019-08-12 09:15
 **/
public class BasePropertiesUtils {

    private static final String BASH_PATH = "jdbc/base.properties";

    private static Properties properties;

    public static String getKey(String key) throws IOException {
        if (properties == null) {
            properties = new Properties();
            try (      InputStream resourceAsStream = BasePropertiesUtils.class.getClassLoader().getResourceAsStream(BASH_PATH);){
                properties.load(resourceAsStream);
            }
        }
        return properties.getProperty(key, "");
    }

    public static String createDb(String db) throws IOException {
        return getKey("create.db").replace("%s",db);
    }

    public static String dropDb(String db) throws IOException {
        return getKey("drop.db").replace("%s",db);
    }

    public static String dropTb(String tb) throws IOException {
        return getKey("drop.tb").replace("%s",tb);
    }

    public static String use(String db) throws IOException {
        return getKey("use.db").replace("%s",db);
    }
    public static String createDbNotExists(String db) throws IOException {
        return getKey("create.ne.db").replace("%s",db);
    }

    public static List<String> getExecuteSqls(String path){
        try (
                InputStream resourceAsStream = BasePropertiesUtils.class.getClassLoader().getResourceAsStream(path);
                BufferedReader br = new BufferedReader(new InputStreamReader(resourceAsStream));
        ){
            List<String> sqls = new ArrayList<>();
            String str;
            while ((str = br.readLine())!=null){
                sqls.add(str);
            }
            return sqls;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
