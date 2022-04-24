package com.test.web.service;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.test.web.config.RabbitMQConfig;
import org.json.JSONObject;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

@Service
public class WatermarkService {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    RabbitMQConfig rabbitMQConfig;

    public void sendWatermarkRequest(String text) throws IOException, TimeoutException {

        Connection conn = getRabbitMqConnection();
        Channel channel = getRabbitMqChannel(conn);

        JSONObject data = new JSONObject();
        data.put("text", text);

        sendMQRequest(channel, data);

        channel.close();
        conn.close();

    }

    private Connection getRabbitMqConnection() throws IOException, TimeoutException {

        ConnectionFactory factory = new ConnectionFactory();

        factory.setUsername(rabbitMQConfig.getUsername());
        factory.setPassword(rabbitMQConfig.getPassword());
        factory.setHost(rabbitMQConfig.getHost());
        factory.setPort(rabbitMQConfig.getPort());

        Connection mConnection = factory.newConnection();

        return mConnection;
    }

    private Channel getRabbitMqChannel(Connection connection) throws IOException {

        Channel channel = connection.createChannel();

        Map<String, Object> arguments = new HashMap<String, Object>();
        arguments.put("x-queue-mode", "lazy");
        channel.queueDeclare(rabbitMQConfig.getSendQueue(), true, false, false, arguments);

        return channel;
    }

    private void sendMQRequest(Channel channel, JSONObject data) throws IOException {
        byte[] convertData = data.toString().getBytes();
        channel.basicPublish("", "WMEmbedMessage", null, convertData);
    }

}
