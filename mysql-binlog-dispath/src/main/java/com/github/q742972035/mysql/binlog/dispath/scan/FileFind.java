package com.github.q742972035.mysql.binlog.dispath.scan;

import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * 文件查找
 *
 * @program: mysql-binlog-dispath
 * @description:
 * @author: 张忆
 * @create: 2019-09-30 20:08
 **/
public class FileFind {

    private static final String STAR = "*";
    private static final String STAR_STAR = STAR + STAR;

    /**
     * 包路径
     */
    private String basePackage;
    private ClassLoader classLoader;
    /**
     * 文件类型
     */
    private String fileType;

    /**
     * 将basePackage的.转换成 '/'
     */
    private String separatorBasePackage;

    /**
     * 获取fileType文件后缀的文件
     */
    private List<InputStream> targetFileIs = new ArrayList<>();

    /**
     * 基于separatorBasePackage的前N的/，其中“前”是基于"**"或者"*"的
     */
    private List<String> pathOfStart = new ArrayList<>();

    /**
     * 记录最前面的*或者**的位置
     */
    private int starIndex = -1;

    final String DEFAULT_RESOURCE_PATTERN;

    public FileFind(String basePackage, String fileType) {
        this(basePackage, fileType, ClassLoader.getSystemClassLoader());
    }

    public FileFind(String basePackage, String fileType, ClassLoader classLoader) {
        this.basePackage = basePackage;
        this.fileType = fileType;
        this.classLoader = classLoader;
        this.separatorBasePackage = this.basePackage.replaceAll("\\.", "/");
        DEFAULT_RESOURCE_PATTERN = "**/*." + fileType;
        init();
    }

    public String getSeparatorBasePackage() {
        return separatorBasePackage;
    }

    public List<InputStream> getTargetFileIs() {
        return targetFileIs;
    }

    private void init() {
        // 判断 * 的第一个索引
        int starIndex = this.separatorBasePackage.indexOf(STAR);
        // 判断 ** 的第一个索引
        int doubleStarIndex = this.separatorBasePackage.indexOf(STAR_STAR);
        if (doubleStarIndex > -1 && starIndex > -1) {
            this.starIndex = Math.min(starIndex, doubleStarIndex);
        } else {
            if (doubleStarIndex > -1) {
                this.starIndex = doubleStarIndex;
            } else if (starIndex > -1) {
                this.starIndex = starIndex;
            }
        }
        String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                resolveBasePackage(this.getSeparatorBasePackage()) + '/' + DEFAULT_RESOURCE_PATTERN;
        try {
            Resource[] resources = getResourcePatternResolver().getResources(packageSearchPath);
            for (Resource resource : resources) {
                if (resource.getURL().getPath().endsWith(this.fileType)){
                    targetFileIs.add(resource.getInputStream());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*if (pathOfStart.size() > 0) {
            for (String path : pathOfStart) {
                if (new PathReSovle(basePackage, path).macth()) {
                    File[] files = new File(path).listFiles();
                    for (File file : files) {
                        if (file.isFile() && file.getName().endsWith(fileType)) {
                            targetFileIs.add(file);
                        }
                    }
                }
            }
        }*/
    }

//    public final MetadataReaderFactory getMetadataReaderFactory() {
//        return new CachingMetadataReaderFactory();
//    }

    String CLASSPATH_ALL_URL_PREFIX = "classpath*:";

    private ResourcePatternResolver getResourcePatternResolver() {
        return new PathMatchingResourcePatternResolver();
    }

    String resolveBasePackage(String basePackage) {
        return ClassUtils.convertClassNameToResourcePath(getEnvironment().resolveRequiredPlaceholders(basePackage));
    }

    public final Environment getEnvironment() {
        return new StandardEnvironment();
    }

}
