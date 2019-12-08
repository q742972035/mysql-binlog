package com.github.q742972035.mysql.binlog.dispatch.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @program: mysql-binlog-dispath
 * @description:
 * @author: 张忆
 * @create: 2019-09-30 22:33
 **/
public class FileUtils {
    /**
     * 基于baseFile（目录）获取目录下的所有目录
     *
     * @param baseFile
     * @return
     */
    public static List<String> getPathByBaseFile(File baseFile) {
        if (baseFile == null || !baseFile.isDirectory()) {
            return Collections.emptyList();
        }
        List<String> dirs = new ArrayList<>();
        dirs.add(baseFile.getPath());
        LinkedList<File> dirFiles = new LinkedList<>();

        File[] files = baseFile.listFiles();
        if (files == null) {
            return dirs;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                dirFiles.add(file);
                dirs.add(file.getPath());
            }
        }


        File dest;
        while ((dest = dirFiles.pollLast()) != null) {
            files = dest.listFiles();
            if (files == null) {
                continue;
            }
            for (File file1 : files) {
                if (file1.isDirectory()) {
                    dirFiles.add(file1);
                    dirs.add(file1.getPath());
                }
            }
        }
        return dirs;
    }
}
