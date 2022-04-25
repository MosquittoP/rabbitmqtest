package com.test.web.controller;

import com.test.web.service.RabbitMQService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Controller
public class RabbitMQController {

    @Autowired
    RabbitMQService rabbitMQService;

    @RequestMapping(value = "/request/{text}")
    public void requestQueue (@PathVariable String text) {
        try {
            rabbitMQService.sendRequest(text);
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

}
