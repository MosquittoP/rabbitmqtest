package com.test.web;

import com.test.web.component.RabbitMQResponseScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AppRunner implements CommandLineRunner {

    @Autowired
    RabbitMQResponseScheduler scheduler;

    @Override
    public void run(String... args) throws Exception {
        try {
            scheduler.receiveMessage();
        } catch (Exception e) {
            e.getStackTrace();
        }
    }
}
