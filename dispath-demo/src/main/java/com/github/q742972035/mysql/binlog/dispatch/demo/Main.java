package com.github.q742972035.mysql.binlog.dispatch.demo;

import com.github.q742972035.mysql.binlog.dispatch.Runner;
import com.github.q742972035.mysql.binlog.dispatch.utils.PropertiesUtils;
import com.github.q742972035.mysql.binlog.expose.build.BinaryLogClientBuild;

import java.io.IOException;
import java.util.Properties;

public class Main {

    public static void main(String[] args) throws IOException {
        Properties properties = PropertiesUtils.getPropertiesByPath("/test.properties", ClassLoader.getSystemClassLoader());
        BinaryLogClientBuild build = new BinaryLogClientBuild()
                .setUsername(properties.getProperty("username"))
                .setPassword(properties.getProperty("password"))
                .setSchema(properties.getProperty("schema"))
                .setHostname(properties.getProperty("hostname"));

        Runner runner = new Runner();
        runner.setClientBuild(build);
        runner.setScanpackage(properties.getProperty("scanpackage"));
        runner.init();
        runner.run();
    }
}
