package com.github.q742972035.mysql.binlog.dispath.utils;

import com.github.q742972035.mysql.binlog.dispath.asm.type.SimpleAnnotationMetadata;
import com.github.q742972035.mysql.binlog.expose.utils.CollectionUtils;

import java.util.List;

public class AllUtils {

    public static boolean containsCLassName(Object obj, String className) {
        if (obj instanceof List) {
            return containsCLassName((List) obj, className);
        }
        return false;
    }

    public static boolean containsCLassName(List list, String className) {
        if (CollectionUtils.isEmpty(list)) {
            return false;
        }
        for (Object o : list) {
            if (o instanceof SimpleAnnotationMetadata
                    && SimpleAnnotationMetadataHandler.containsCLassName((SimpleAnnotationMetadata) o, className)) {
                return true;
            }
        }
        return false;
    }


    private static class SimpleAnnotationMetadataHandler {

        static boolean containsCLassName(SimpleAnnotationMetadata metadata, String className) {
            return metadata.getClassName().equals(className);
        }


    }
}
