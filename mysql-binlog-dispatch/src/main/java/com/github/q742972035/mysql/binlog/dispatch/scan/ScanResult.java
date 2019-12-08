package com.github.q742972035.mysql.binlog.dispatch.scan;

import com.github.q742972035.mysql.binlog.dispatch.annotation.TableHandler;
import com.github.q742972035.mysql.binlog.dispatch.asm.SimpleAnnotationMetadataReadingVisitor;
import com.github.q742972035.mysql.binlog.dispatch.asm.type.SimpleAnnotationMetadata;
import com.github.q742972035.mysql.binlog.dispatch.exception.TableHandlerExistException;
import com.github.q742972035.mysql.binlog.dispatch.scan.filter.HasRegistryClassLoaderContextFilter;
import org.objectweb.asm.ClassReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * 扫描结果
 *
 * @program: mysql-binlog-dispath
 * @description:
 * @author: 张忆
 * @create: 2019-10-02 03:01
 **/
public class ScanResult {
    private PackageScanner packageScanner;
    private static final Logger LOGGER = LoggerFactory.getLogger(ScanResult.class);

    private static final int PARSING_OPTIONS = ClassReader.SKIP_DEBUG
            | ClassReader.SKIP_CODE | ClassReader.SKIP_FRAMES;

    public ScanResult(PackageScanner packageScanner) {
        this.packageScanner = packageScanner;
    }

    public List<SimpleAnnotationMetadata> annotationMetadatas() {
        List<SimpleAnnotationMetadata> metadatas = new ArrayList<>();
        packageScanner.allClassInputStream().forEach(is -> {
            SimpleAnnotationMetadataReadingVisitor visitor = new SimpleAnnotationMetadataReadingVisitor(packageScanner.getClassLoader());
            try {
                new ClassReader(is).accept(visitor, PARSING_OPTIONS);
            } catch (IOException e) {
                e.printStackTrace();
            }
            SimpleAnnotationMetadata metadata = visitor.getMetadata();
            metadatas.add(metadata);
        });
        return metadatas;
    }


    /**
     * 记录表明和已经存在的className关系
     */
    private final Map<String, String> tableNameMap = Collections.synchronizedMap(new HashMap<>());

    /**
     * 初始化ClassLoaderContext
     */
    public void initContext() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        // 1. 从annotationMetadatas()找出所有包含@TableHandler的类
        List<SimpleAnnotationMetadata> metadatas = annotationMetadatas();
        for (SimpleAnnotationMetadata metadata : metadatas) {
            if (metadata.getAnnotationTypes().contains(TableHandler.class.getName())) {
                List<Object> tableName = metadata.getAllAnnotationAttributes(TableHandler.class.getName()).get("tableName");
                HasRegistryClassLoaderContextFilter.registry(metadata.getClassName());

                // 2. 依次执行zy.opsource.dispath.scan.ClassLoaderContextFactory.createIfNotExist方法初始化
                ClassLoaderContextFactory.TABLE_HANDLER_NAME_CONTEXT_MAP.put((String) tableName.get(0), ClassLoaderContextFactory.createIfNotExist(metadata.getClassName()));
                if (tableNameMap.containsKey((String) tableName.get(0))) {
                    throw new TableHandlerExistException(String.format("TableHandler[%s] 已经存在对应的类[%s]", (String) tableName.get(0), tableNameMap.get((String) tableName.get(0))));
                }
                tableNameMap.put((String) tableName.get(0), metadata.getClassName());
            }
        }
    }

}
