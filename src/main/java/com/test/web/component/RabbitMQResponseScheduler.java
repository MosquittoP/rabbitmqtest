package com.test.web.component;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import com.test.web.config.RabbitMQConfig;
import com.test.web.util.ZipUtil;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Component
public class RabbitMQResponseScheduler {

    @Autowired
    RabbitMQConfig rabbitMQConfig;

    public void receiveMessage() throws IOException, TimeoutException {

        ConnectionFactory factory = new ConnectionFactory();

        factory.setUsername(rabbitMQConfig.getUsername());
        factory.setPassword(rabbitMQConfig.getPassword());
        factory.setHost(rabbitMQConfig.getHost());
        factory.setPort(rabbitMQConfig.getPort());

        Connection conn = factory.newConnection();
        Channel channel = conn.createChannel();

        channel.queueDeclare(rabbitMQConfig.getSendQueue(), true, false, false, null);

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String messageString = new String(delivery.getBody(), "UTF-8");
            JSONObject message = new JSONObject(messageString);

            String responseText = message.getString("text");
            System.out.println(responseText);

            ZipUtil zipUtil = new ZipUtil();
            String fileName = responseText + ".zip";
            zipUtil.zip("/Users/cheil/Desktop/ziptest", fileName);
        };

        channel.basicConsume(rabbitMQConfig.getSendQueue(), true, deliverCallback, consumerTag -> {});

    }
}
