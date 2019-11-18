package com.github.q742972035.mysql.binlog.dispath.scan;

import org.junit.Before;
import org.junit.Test;
import org.objectweb.asm.ClassReader;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class FileFindTest {

    private static final int PARSING_OPTIONS = ClassReader.SKIP_DEBUG
            | ClassReader.SKIP_CODE | ClassReader.SKIP_FRAMES;


    /**
     * 目录根路径
     */
    public String basePath;

    @Before
    public void setup() {
        basePath = ClassLoader.getSystemClassLoader().getResource("").getPath();
    }

    /**
     * 测试全路径
     */
    @Test
    public void testFullPath() throws IOException {
        new FileFind("zy.opsource.dispath.scan","class");
    }



}