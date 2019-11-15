package com.github.q742972035.mysql.binlog.expose.utils;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/**
 * @program: mysql-binlog-incr-expose
 * @description
 * @author: zy
 * @create: 2019-08-13 16:01
 **/
public class MathUtils {
    public static List<Integer> getOdds(int size) {
        List<Integer> list = new ArrayList<>((size + 1) / 2);
        for (int i = 0; i <= size; i++) {
            if (i % 2 == 0) {
                list.add(i);
            }
        }
        return list;
    }

    public static List<Long> getIndexList(BitSet bitSet) {
        List<Long> list = new ArrayList<>();
        String s = bitSet.toString().replaceFirst("\\{", "").replaceFirst("}", "");
        String[] split = s.split(",");
        for (String s1 : split) {
            list.add(Long.valueOf(s1.trim()));
        }
        return list;
    }


}
