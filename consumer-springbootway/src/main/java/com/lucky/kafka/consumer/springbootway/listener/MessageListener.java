package com.lucky.kafka.consumer.springbootway.listener;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class MessageListener {

    private static final Logger LOG = LoggerFactory.getLogger(MessageListener.class);

    @KafkaListener(topics = "${kafka.topic.name}")
    public void receive(@Payload String data,
                        @Header(KafkaHeaders.OFFSET) Long offset
                        //@Header(KafkaHeaders.CONSUMER) KafkaConsumer<String, String> consumer,
                        //@Header(KafkaHeaders.TIMESTAMP_TYPE) String timestampType,
                        //@Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                        //@Header(KafkaHeaders.RECEIVED_PARTITION_ID) Integer partitionId,
                        //@Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String messageKey,
                        //@Header(KafkaHeaders.RECEIVED_TIMESTAMP) Long timestamp,
                        //@Header("X-Custom-Header") String customHeader
    )
    {

        LOG.info("- - - - - - - - - - - - - - -");
        LOG.info("received message='{}'", data);
        //LOG.info("consumer: {}", consumer);
        //LOG.info("topic: {}", topic);
        //LOG.info("message key: {}", messageKey);
        //LOG.info("partition id: {}", partitionId);
        LOG.info("offset: {}", offset);
//        LOG.info("timestamp type: {}", timestampType);
//        LOG.info("timestamp: {}", timestamp);
//        LOG.info("custom header: {}", customHeader);
    }

    //@KafkaListener(topics = "${kafka.topic.name}")
    public void receive(@Payload String data,
                        @Headers MessageHeaders messageHeaders) {

        LOG.info("- - - - - - - - - - - - - - -");
        LOG.info("received message='{}'", data);
        messageHeaders.keySet().forEach(key -> {
            Object value = messageHeaders.get(key);
            //if (key.equals("X-Custom-Header")){
            if(value instanceof byte[]){
                LOG.info("{}: {}", key, new String((byte[])value));
            } else {
                LOG.info("{}: {}", key, value);
            }
        });

    }
}
