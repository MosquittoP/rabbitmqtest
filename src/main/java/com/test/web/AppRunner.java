package com.test.web;

import com.test.web.component.RabbitMQRequestScheduler;
import com.test.web.component.RabbitMQResponseScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AppRunner implements CommandLineRunner {

    @Autowired
    RabbitMQRequestScheduler requestScheduler;

    @Autowired
    RabbitMQResponseScheduler responseScheduler;

    @Override
    public void run(String... args) throws Exception {
        try {
            requestScheduler.receiveMessage();
            responseScheduler.receiveMassage();
        } catch (Exception e) {
            e.getStackTrace();
        }
    }
}
