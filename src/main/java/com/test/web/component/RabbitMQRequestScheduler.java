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
1번 Queue에 들어있는 요청을 받아와 압축 실행 후 작업 결과를 2번 Queue로 보내는 역할
 */

@Component
public class RabbitMQRequestScheduler {

    @Autowired
    RabbitMQConfig rabbitMQConfig;

    public void receiveMessage() throws IOException, TimeoutException {

        ConnectionFactory factory = new ConnectionFactory(); // 연결할 Queue 세팅

        factory.setUsername(rabbitMQConfig.getUsername());
        factory.setPassword(rabbitMQConfig.getPassword());
        factory.setHost(rabbitMQConfig.getHost());
        factory.setPort(rabbitMQConfig.getPort());

        Connection conn = factory.newConnection();
        Channel channel = conn.createChannel();

        channel.queueDeclare(rabbitMQConfig.getSendQueue(), true, false, false, null); // 1번 Queue에 연결

        DeliverCallback deliverCallback = (consumerTag, delivery) -> { // Queue에서 메시지를 받아오면서 실행하는 로직

            /* 넘겨받는 메시지
             * id : 현재 작업 고유 번호
             * zipId : 작업 요청 고유 번호
             * wmSeq : 작업이 필요한 현재 워터마크
             * prdName : 기기명
             * resolution : 화질 (HI/LOW)
             * filePath : 작업 경로
             * */
            String messageString = new String(delivery.getBody(), "UTF-8");
            JSONObject message = new JSONObject(messageString);

            String responseText = message.getString("text");
            System.out.println(responseText);

            String result;
            ZipUtil zipUtil = new ZipUtil();
            String fileName = responseText + ".zip"; // zip 파일명은 prdName + "_" + resolution + ".zip"

            // 현재는 함수 실행 완료 여부만 확인 중
            // 압축 파일 존재 확인, 용량 및 압축파일 유효 확인 필요
            try {
                zipUtil.zip("/Users/cheil/Desktop/ziptest", fileName);
                result = "Success";
                // 성공 메시지
            } catch (Exception e) {
                e.getStackTrace();
                result = "Fail";
                // 살패 매시지
            }

            /* 넘겨주는 결과 메시지
             * id : 현재 작업 고유 번호 (RSVP에서 넘겨줌)
             * zipId : 작업 요청 고유 번호 (RSVP에서 넘겨줌)
             * wmSeq : 작업한 워터마크 (RSVP에서 넘겨줌)
             * complete : 작업 결과 ('Y' - 성공, 'F' - 실패)
             * */
            channel.queueDeclare(rabbitMQConfig.getReceiveQueue(), true, false, false, null); // 2번 Queue에 연결
            JSONObject data = new JSONObject(); // data는 json 데이터로 던져주기
            data.put("result", result);
            byte[] convertData = data.toString().getBytes();
            channel.basicPublish("", rabbitMQConfig.getReceiveQueue(), null, convertData);
        };

        channel.basicConsume(rabbitMQConfig.getSendQueue(), true, deliverCallback, consumerTag -> {}); // Queue 받아오기

    }
}
