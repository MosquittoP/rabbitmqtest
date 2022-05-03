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

/*
압축 결과를 받아와 출력
 */

@Component
public class RabbitMQResponseScheduler {

    @Autowired
    RabbitMQConfig rabbitMQConfig;

    public void receiveMassage() throws IOException, TimeoutException {

        ConnectionFactory factory = new ConnectionFactory();

        factory.setUsername(rabbitMQConfig.getUsername());
        factory.setPassword(rabbitMQConfig.getPassword());
        factory.setHost(rabbitMQConfig.getHost());
        factory.setPort(rabbitMQConfig.getPort());

        Connection conn = factory.newConnection();
        Channel channel = conn.createChannel();

        channel.queueDeclare(rabbitMQConfig.getReceiveQueue(), true, false, false, null);

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String messageString = new String(delivery.getBody(), "UTF-8");
            JSONObject message = new JSONObject(messageString);

            String responseText = message.getString("result");
            System.out.println(responseText);
        };

        channel.basicConsume(rabbitMQConfig.getReceiveQueue(), true, deliverCallback, consumerTag -> {});

    }

}
