package com.github.q742972035.mysql.binlog.dispatch;

import com.alibaba.druid.pool.DruidDataSource;
import com.github.q742972035.mysql.binlog.dispatch.scan.PackageScanner;
import com.github.q742972035.mysql.binlog.dispatch.utils.PropertiesUtils;
import com.github.q742972035.mysql.binlog.expose.build.BinaryLogClientBuild;
import org.junit.Test;

import java.io.IOException;
import java.util.Properties;

public class RunnerTest {


    @Test
    public void testProperties() throws IOException {
        Properties properties = PropertiesUtils.getPropertiesByPath("/test.properties", ClassLoader.getSystemClassLoader());
        BinaryLogClientBuild build = new BinaryLogClientBuild()
                .setUsername(properties.getProperty("username"))
                .setPassword(properties.getProperty("password"))
                .setSchema(properties.getProperty("schema"))
                .setHostname(properties.getProperty("hostname"));

        Runner runner = new Runner();
        runner.setClientBuild(build);
        runner.setScanpackage(properties.getProperty("scanpackage"));
        runner.setDataSource(createDruidDataSource(properties));
        PackageScanner scanner = new PackageScanner();
        scanner.scan(properties.getProperty("scanpackage").split(","));
        runner.setThreadSize(1);
        runner.init();
        runner.run();
    }

    private DruidDataSource createDruidDataSource(Properties properties) {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setUrl("jdbc:mysql://" + properties.getProperty("hostname") + ":3306/" + properties.getProperty("schema") + "?useUnicode=true&characterEncoding=UTF8");
        druidDataSource.setUsername(properties.getProperty("username"));
        druidDataSource.setPassword(properties.getProperty("password"));
        druidDataSource.setMaxActive(500);
        druidDataSource.setMinIdle(1);
        druidDataSource.setValidationQuery("select 1");
        druidDataSource.setTestOnBorrow(false);
        druidDataSource.setTestOnReturn(false);
        druidDataSource.setTestWhileIdle(true);
        return druidDataSource;
    }
}
