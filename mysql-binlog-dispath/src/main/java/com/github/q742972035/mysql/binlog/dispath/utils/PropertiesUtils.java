package com.github.q742972035.mysql.binlog.dispath.utils;


import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @program: database-move
 * @description
 * @author: zy
 * @create: 2019-05-22 15:13
 **/
public class PropertiesUtils {
    private static final Map<String, Properties> PPM = new HashMap<>();

    /**
     * 记录原先的语句
     */
    private static final Map<String, Map<String, String>> SRC_KEY_CONTENT_MAP = new HashMap<>();

    /**
     * 获取原先的拥有通配符的key内容
     *
     * @return
     */
    public static String getFuzzyKeyContent(String path, String key) {
        return SRC_KEY_CONTENT_MAP.get(path).get(key);
    }

    private static final String SPE_START = "${";
    private static final String SPE_END = "}";

    public static Properties getPropertiesByPath(String path, ClassLoader classLoader) {
        Properties p;
        if ((p = PPM.get(path)) != null) {
            return p;
        }
        while (path.startsWith("/")) {
            path = path.substring(1);
        }
        try (InputStream inputStream = classLoader.getResourceAsStream(path);) {
            p = new Properties();
            p.load(inputStream);

            reset(path, p);
            PPM.put(path, p);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return p;
    }

    private static void reset(String path, Properties p) {
        Map<String, String> content = new HashMap<>();
        SRC_KEY_CONTENT_MAP.put(path, content);
        for (Map.Entry<Object, Object> entry : p.entrySet()) {
            if (entry.getKey() instanceof String && entry.getValue() instanceof String) {
                String key = (String) entry.getKey();
                String value = (String) entry.getValue();
                List<String> speList = getSpeList(value);
                if (speList.size() > 0) {
                    content.put(key, value);
                    for (String s : speList) {
                        value = value.replace(SPE_START + s + SPE_END, p.getProperty(s));
                    }
                    p.setProperty(key, value);
                }
            }
        }
    }

    /**
     * 获取str中包含${} 中的内部字符串
     *
     * @param str
     * @return
     */
    private static List<String> getSpeList(String str) {
        List<String> list = new ArrayList<>();
        int speStart = 0;
        int speEnd = 0;

        do {
            speStart = str.indexOf(SPE_START, speStart);
            if (speStart != -1) {
                speEnd = str.indexOf(SPE_END, speEnd);
                if (speEnd != -1) {
                    list.add(str.substring(speStart + SPE_START.length(), speEnd));
                }
                speStart++;
                speEnd++;
            } else {
                break;
            }
        } while (true);
        return list;
    }
}
