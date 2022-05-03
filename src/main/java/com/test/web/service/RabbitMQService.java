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

/*
Controller에서 들어온 요청에 따라 Queue에 압축파일 생성을 요청
 */

@Service
public class RabbitMQService {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    RabbitMQConfig rabbitMQConfig;

    public void sendRequest(String text) throws IOException, TimeoutException {

        Connection conn = getRabbitMqConnection();
        Channel channel = getRabbitMqChannel(conn);

        JSONObject data = new JSONObject(); // 메시지는 json으로 전송
        data.put("text", text);

        sendMQRequest(channel, data);

        channel.close();
        conn.close();

    }

    private Connection getRabbitMqConnection() throws IOException, TimeoutException { // Queue 설정 후 Rabbitmq 서버 연결

        ConnectionFactory factory = new ConnectionFactory();

        factory.setUsername(rabbitMQConfig.getUsername());
        factory.setPassword(rabbitMQConfig.getPassword());
        factory.setHost(rabbitMQConfig.getHost());
        factory.setPort(rabbitMQConfig.getPort());

        Connection mConnection = factory.newConnection();

        return mConnection;
    }

    private Channel getRabbitMqChannel(Connection connection) throws IOException { // 채널에 1번 Queue를 연결

        Channel channel = connection.createChannel();

        Map<String, Object> arguments = new HashMap<String, Object>();
        channel.queueDeclare(rabbitMQConfig.getSendQueue(), true, false, false, null);

        return channel;
    }

    private void sendMQRequest(Channel channel, JSONObject data) throws IOException {
        byte[] convertData = data.toString().getBytes();
        channel.basicPublish("", rabbitMQConfig.getSendQueue(), null, convertData); // Queue에 메시지를 보내는 메소드
    }

}
