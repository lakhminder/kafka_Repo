package com.lucky.kafka.producer.springbootway.service;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaderMapper;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.kafka.support.converter.MessagingMessageConverter;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import javax.annotation.Resource;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *  check produceEvents() to produce events
 *
 *  for callback refer publishMessage() and nested KafkaCallback class
 *
 *  3 ways to publish msg
 *  1 refer publishMessage()
 *  2 refer publishMessageWithHeader()
 *  3 refer publishMessageWithHeaderViaProducerRecord()
 *
 */
@Service
@EnableScheduling
//To disable scheduler in Test
@ConditionalOnProperty(
        name = "kafka.publisher.scheduler.enabled",
        havingValue = "true",
        matchIfMissing = true)
public class RawMessagePublisher {
    private static final Logger LOG = LoggerFactory.getLogger(RawMessagePublisher.class);

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    @Value(value = "${kafka.topic.name}")
    private String topic;

    public void publishMessage(String msg){
        ListenableFuture<SendResult<String, String>> sendResultListenableFuture = kafkaTemplate.send(topic, msg);
        //sendResultListenableFuture.get() // will block the thread , use callback for async
        sendResultListenableFuture.addCallback(new KafkaCallback<>(msg));

        //Refer KafkaCallback nested class
        /*sendResultListenableFuture.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
            @Override
            public void onFailure(Throwable ex) {
                log.error("Error while sending msg: " + msg, ex);
            }

            @Override
            public void onSuccess(SendResult<String, String> result) {
                //result.getProducerRecord().value();
                log.info("Sent message=[" + msg +"] with offset=[" + result.getRecordMetadata().offset() + "]");
            }
        });*/
    }

    /**
     * method 2 require Jackson lib as dependency.. refer build.gradle
     * else set MessageConverter to kafkaTemplate to just convert String into bytes
     *
     *better to use jackson serializer as they are more mature (self)
     * @param msg
     */
    public void publishMessageWithHeader(String msg){
        Message<String> message = MessageBuilder
                .withPayload(msg)
                .setHeader(KafkaHeaders.TOPIC, topic)
                //.setHeader(KafkaHeaders.MESSAGE_KEY, "999")
                //.setHeader(KafkaHeaders.PARTITION_ID, 0)
                .setHeader("X-Custom-Header", "Sending Custom Header with Spring Kafka Message Builder")
                .build();
        /*MessagingMessageConverter msgConverter = new MessagingMessageConverter();
        msgConverter.setHeaderMapper(new KafkaHeaderMapper() {
            @Override
            public void fromHeaders(MessageHeaders headers, Headers target) {
                headers.forEach((k,v) -> target.add(k,v != null ? v.toString().getBytes(Charset.forName("UTF-8")): null));
            }

            @Override
            public void toHeaders(Headers source, Map<String, Object> target) {

            }
        });
        kafkaTemplate.setMessageConverter(msgConverter);*/
        ListenableFuture<SendResult<String, String>> sendResultListenableFuture = kafkaTemplate.send(message);
        sendResultListenableFuture.addCallback(new KafkaCallback<>(msg));
    }


    //Method 3
    public void publishMessageWithHeaderViaProducerRecord(String msg){
        List<Header> headers = new ArrayList<>();
        headers.add(new RecordHeader("X-Custom-Header", "Sending Custom Header with Spring Kafka".getBytes()));

        Object key = null;
        Object val = msg;
        Integer partition = null;
        ProducerRecord<String, String> bar = new ProducerRecord<>(topic, null, null,null, msg, headers);
        //LOG.info("sending message='{}' to topic='{}'", data, topicBar);

        ListenableFuture<SendResult<String, String>> sendResultListenableFuture = kafkaTemplate.send(bar);
        sendResultListenableFuture.addCallback(new KafkaCallback<>(msg));
    }




    @Scheduled(fixedRate = 10000)//10 sec
    public void produceEvents(){
        String event = "Sending msg with time " + ZonedDateTime.now();
        //publishMessage(event);

        //publishMessageWithHeaderViaProducerRecord(event);
        publishMessageWithHeader(event);
    }




    private static class KafkaCallback<T extends SendResult> implements ListenableFutureCallback<T> {
        private String message;

        public KafkaCallback(String msg){
            this.message = msg;
        }
        @Override
        public void onFailure(Throwable ex) {
            LOG.error("Error while sending msg: " + message, ex);
        }

        @Override
        public void onSuccess(T result) {
            LOG.info("Sent message=[" + message +"] with offset=[" + result.getRecordMetadata().offset() + "]");
        }
    }
}
