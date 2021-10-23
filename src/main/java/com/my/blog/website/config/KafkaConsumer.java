package com.my.blog.website.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
public class KafkaConsumer {
    @KafkaListener(topics = "topic_one", groupId = "firstGroup")
    public void processMessage(ConsumerRecord<?, ?> record) {
        log.info("kafka processMessage start");
        log.info("processMessage, topic = {}, msg = {}", record.topic(), record.value());

        // do something ...

        log.info("kafka processMessage end");
    }
}
