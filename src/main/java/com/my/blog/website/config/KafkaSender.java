package com.my.blog.website.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.my.blog.website.kafklademo.demo.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import javax.annotation.Resource;
import java.util.Date;

@Component
@Slf4j
public class KafkaSender {

    @Resource
    private KafkaTemplate kafkaTemplate;

    private Gson gson = new GsonBuilder().create();

    public void send(String msg){
        Message message = new Message();
        message.setId(System.currentTimeMillis());
        message.setSendTime(new Date());
        message.setMsg(msg);
        log.info("start send kafka message:{}", message);
        String topic = "topic_one";
        ListenableFuture<SendResult<String, String>> topic_one = kafkaTemplate.send(topic, gson.toJson(message));
        // 是可以处理当前队列的消息失败成功处理
        topic_one.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
            @Override
            public void onFailure(Throwable ex) {
                log.error("kafka sendMessage error, ex = {}, topic = {}, data = {}", ex, topic, gson.toJson(message));
            }

            @Override
            public void onSuccess(SendResult<String, String> stringStringSendResult) {
                log.info("kafka sendMessage success topic = {}, data = {}",topic, gson.toJson(message));
            }
        });
        log.info("kafka send message success");
    }
}
