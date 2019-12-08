package com.github.q742972035.mysql.binlog.dispatch.utils;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;

public class FileUtilsTest {

    /**
     * 目录根路径
     */
    public String basePath;

    @Before
    public void setup() {
        basePath = ClassLoader.getSystemClassLoader().getResource("").getPath();
    }

    @Test
    public void testFileUtils() {
        String path = "zy/opsource/asm";
        File file = new File(basePath + "/" + path);
        List<String> pathByBaseFile = FileUtils.getPathByBaseFile(file);
    }
}