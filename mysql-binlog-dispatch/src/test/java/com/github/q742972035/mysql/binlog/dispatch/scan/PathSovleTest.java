package com.github.q742972035.mysql.binlog.dispatch.scan;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

public class PathSovleTest {

    /**
     * 目录根路径
     */
    public String basePath;

    @Before
    public void setup() {
        basePath = ClassLoader.getSystemClassLoader().getResource("").getPath();
    }

    @Test
    public void testPathSovle() {
        assertThat(new PathReSovle("zy.opsource.dispath.scan", basePath + "zy\\opsource\\dispath\\scan").macth()).isEqualTo(true);
        assertThat(new PathReSovle("zy.**", basePath + "zy\\opsource\\dispath\\scan").macth()).isEqualTo(true);
        assertThat(new PathReSovle("zy.dispath.**", basePath + "zy\\opsource\\dispath\\scan").macth()).isEqualTo(false);
        assertThat(new PathReSovle("**.dispath.scan", basePath + "zy\\opsource\\dispath\\scan").macth()).isEqualTo(true);
        assertThat(new PathReSovle("zy.**.scan", basePath + "zy\\opsource\\dispath\\scan").macth()).isEqualTo(true);
        assertThat(new PathReSovle("**.scan", basePath + "zy\\opsource\\dispath\\scan").macth()).isEqualTo(true);
        assertThat(new PathReSovle("**.53.scan", basePath + "zy\\opsource\\dispath\\scan").macth()).isEqualTo(false);

        assertThat(new PathReSovle("zy.opsource.dispath.scan.testmore", basePath + "zy\\opsource\\dispath\\scan\\testmore").macth()).isEqualTo(true);
        assertThat(new PathReSovle("**.dispath.*.testmore", basePath + "zy\\opsource\\dispath\\scan\\testmore").macth()).isEqualTo(true);
        assertThat(new PathReSovle("**.dispath.*", basePath + "zy\\opsource\\dispath\\scan\\testmore").macth()).isEqualTo(false);
        assertThat(new PathReSovle("**.dispath.**", basePath + "zy\\opsource\\dispath\\scan\\testmore").macth()).isEqualTo(true);
        assertThat(new PathReSovle("**", basePath + "zy\\opsource\\dispath\\scan\\testmore").macth()).isEqualTo(true);
    }

}