package com.github.q742972035.mysql.binlog.dispatch.scan;

import com.github.q742972035.mysql.binlog.dispatch.exception.PathReSovleException;

import java.io.File;

/**
 * 路径解析
 *
 * @program: mysql-binlog-dispath
 * @description:
 * @author: 张忆
 * @create: 2019-09-30 23:46
 **/
public class PathReSovle {

    private String basePackage;
    /**
     * classLoader 下的目录
     */
    private String basePath;
    private String path;
    private ClassLoader classLoader;

    private String separatorBasePackage;

    private String[] subPackages;
    private String[] subRemainPaths;

    public PathReSovle(String basePackage, String path) {
        this(basePackage, path, ClassLoader.getSystemClassLoader());
    }

    public PathReSovle(String basePackage, String path, ClassLoader classLoader) {
        this.basePackage = basePackage;
        this.path = path.replace("" + File.separator, "/");
        this.basePath = classLoader.getResource("").getPath();
        String osName = System.getProperty("os.name");
        if (osName.startsWith("Win") || osName.startsWith("win")) {
            if (this.path.startsWith("/")) {
                this.path = this.path.substring(1);
            }
            if (this.basePath.startsWith("/")) {
                this.basePath = this.basePath.substring(1);
            }
        }
        this.separatorBasePackage = this.basePackage.replaceAll("\\.", "/");
        this.subPackages = this.separatorBasePackage.split("/");
        this.classLoader = classLoader;
        this.subRemainPaths = this.path.substring(this.basePath.length()).split("/");
    }

    public boolean macth() {
        if (subPackages.length == 1 && "**".equals(subPackages[0])) {
            return true;
        }
        if (subPackages.length == 1 && "*".equals(subPackages[0])) {
            throw new PathReSovleException();
        }
        int length = subPackages.length;
        int resolveIndex = 0;

        int parsedLength = subRemainPaths.length;
        int parseIndex = 0;

        while (resolveIndex < length) {
            String resolveString = subPackages[resolveIndex++];
            if ("**".equals(resolveString)) {
                if (resolveIndex == length) {
                    return true;
                }
                String nextResolveString = subPackages[resolveIndex];

                while (parseIndex < parsedLength) {
                    String parsedString = subRemainPaths[parseIndex++];
                    if (nextResolveString.equals(parsedString)) {
                        parseIndex--;
                        break;
                    }
                }
                if (parseIndex == parsedLength) {
                    return false;
                }
            } else if ("*".equals(resolveString)) {
                if (resolveIndex == length) {
                    if (parsedLength - 1 - parseIndex > 0) {
                        return false;
                    }
                    return true;
                }
                String nextResolveString = subPackages[resolveIndex];
                // 统计不符合次数
                int inconformityCount = 0;
                while (parseIndex < parsedLength) {
                    if (inconformityCount > 1) {
                        return false;
                    }

                    String parsedString = subRemainPaths[parseIndex++];
                    if (nextResolveString.equals(parsedString)) {
                        parseIndex--;
                        break;
                    } else {
                        inconformityCount++;
                    }
                }


            } else {
                if (!resolveString.equals(subRemainPaths[parseIndex++])) {
                    return false;
                }

                if (resolveIndex == length) {
                    if (!resolveString.equals(subRemainPaths[subRemainPaths.length - 1])) {
                        return false;
                    }

                }
            }
        }
        // 带解析的字符串
        return true;
    }
}
