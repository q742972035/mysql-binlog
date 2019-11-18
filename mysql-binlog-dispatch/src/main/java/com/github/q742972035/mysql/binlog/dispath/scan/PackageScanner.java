package com.github.q742972035.mysql.binlog.dispath.scan;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 包扫描器
 *
 * @program: mysql-binlog-dispath
 * @description
 * @author: zy
 **/
public class PackageScanner {
    private static final Logger LOGGER = LoggerFactory.getLogger(PackageScanner.class);

    private ClassLoader classLoader;

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public void scan(String... packages) {
        for (String pack : packages) {
            scan(pack);
        }
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    private final Map<String, FileFind> packageFileFindMap = new HashMap<>();

    public void scan(String packag) {
        if (!packageFileFindMap.containsKey(packag)) {
            if (LOGGER.isDebugEnabled()){
                LOGGER.debug(String.format("扫描包[ %s ].",packag));
            }
            packageFileFindMap.putIfAbsent(packag, new FileFind(packag, "class"));
        }
    }

    public List<InputStream> allClassInputStream() {
        List<InputStream> is = new ArrayList<>();
        packageFileFindMap.forEach((packag, fileFind) -> is.addAll(fileFind.getTargetFileIs()));
        return is;
    }
}
