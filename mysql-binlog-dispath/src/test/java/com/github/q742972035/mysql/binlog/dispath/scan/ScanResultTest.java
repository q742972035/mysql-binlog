package com.github.q742972035.mysql.binlog.dispath.scan;

import com.github.q742972035.mysql.binlog.dispath.annotation.dml.Delete;
import com.github.q742972035.mysql.binlog.dispath.annotation.dml.Insert;
import com.github.q742972035.mysql.binlog.dispath.annotation.dml.Update;
import com.github.q742972035.mysql.binlog.dispath.asm.type.SimpleAnnotationMetadata;
import com.github.q742972035.mysql.binlog.dispath.scan.tablehandler.AdminTableHandler;
import com.github.q742972035.mysql.binlog.dispath.scan.tablehandler.NoTableHandlerAnnitation;
import com.github.q742972035.mysql.binlog.dispath.scan.tablehandler.UserTableHandler;
import com.github.q742972035.mysql.binlog.dispath.utils.AllUtils;
import com.github.q742972035.mysql.binlog.expose.utils.ReflectionUtils;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class ScanResultTest {


    @Test
    public void testScanResult() throws NoSuchMethodException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException {
        PackageScanner scanner = new PackageScanner();
        scanner.scan("zy.opsource.dispath.scan.tablehandler");
        ScanResult scanResult = new ScanResult(scanner);
        List<SimpleAnnotationMetadata> simpleAnnotationMetadata = scanResult.annotationMetadatas();

        // 通过asm扫描包找到三个类
        Assertions.assertThat(AllUtils.containsCLassName(simpleAnnotationMetadata, AdminTableHandler.class.getName())).isTrue();
        assertThat(AllUtils.containsCLassName(simpleAnnotationMetadata, UserTableHandler.class.getName())).isTrue();
        assertThat(AllUtils.containsCLassName(simpleAnnotationMetadata, NoTableHandlerAnnitation.class.getName())).isTrue();

        scanResult.initContext();

        ClassLoaderContext adminTableHandlerContext = ClassLoaderContextFactory.createIfNotExist(AdminTableHandler.class.getName());
        ClassLoaderContext userTableHandlerContext = ClassLoaderContextFactory.createIfNotExist(UserTableHandler.class.getName());
        ClassLoaderContext noTableHandlerAnnitationContext = ClassLoaderContextFactory.createIfNotExist(NoTableHandlerAnnitation.class.getName());


        assertThat(adminTableHandlerContext).isNotNull();
        assertThat(userTableHandlerContext).isNotNull();
        assertThat(noTableHandlerAnnitationContext).isNull();


        assertThat(adminTableHandlerContext.getSource().getClass()).isEqualTo(AdminTableHandler.class);
        assertThat(userTableHandlerContext.getSource().getClass()).isEqualTo(UserTableHandler.class);


        Method getMethodMetadataByAnnotationMethod = ReflectionUtils.findMethod(ClassLoaderContext.class, "getMethodMetadataByAnnotation", Class.class);
        getMethodMetadataByAnnotationMethod.setAccessible(true);


        MethodMetadata adminInsert = (MethodMetadata) getMethodMetadataByAnnotationMethod.invoke(adminTableHandlerContext,Insert.class);
        MethodMetadata adminUpdate = (MethodMetadata) getMethodMetadataByAnnotationMethod.invoke(adminTableHandlerContext,Update.class);
        MethodMetadata adminDelete = (MethodMetadata) getMethodMetadataByAnnotationMethod.invoke(adminTableHandlerContext,Delete.class);

        assertThat(adminInsert).isNull();
        assertThat(adminUpdate).isNull();
        assertThat(adminDelete).isNotNull();

    }






    private boolean contains(List<File> files, String name){
        for (File file : files) {
            if (file.getName().endsWith(name)) {
                return true;
            }
        }
        return false;
    }
}