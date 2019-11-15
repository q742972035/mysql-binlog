package com.github.shyiko.mysql.binlog;

import com.github.shyiko.mysql.binlog.event.Event;
import org.junit.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class BinaryLogClientTest {


    @Test
    public void test() throws IOException {
        BinaryLogClient client = new BinaryLogClient("106.12.138.136",3306,"test_binlog","award3","87654321");
        client.setServerId(3L);
        client.registerEventListener(new BinaryLogClient.EventListener() {
            @Override
            public void onEvent(Event event) {
                System.out.println(event);
            }
        });
        client.connect();

    }

}