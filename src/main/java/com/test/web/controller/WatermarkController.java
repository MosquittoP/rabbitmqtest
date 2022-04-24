package com.test.web.controller;

import com.test.web.service.WatermarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Controller
public class WatermarkController {

    @Autowired
    WatermarkService watermarkService;

    @RequestMapping(value = "/request/{text}")
    public void requestWM (@PathVariable String text) {
        try {
            watermarkService.sendWatermarkRequest(text);
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

}
