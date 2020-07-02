package com.lucky.kafka.consumer.springbootway.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.support.DefaultKafkaHeaderMapper;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.HashMap;
import java.util.Map;

/**
 * @EnableKafka //This is for @KafkaListener activation
 *
 * you can also use AsynListener for setting concurrent listeners.. refer google
 */
//@EnableKafka //This is not needed as Spring Boot come with an auto-configuration for Spring Kafka
@Configuration
public class KafkaConsumerConfig {

    @Value("${kafka.bootstrapServers}")
    private String bootstrapServers;

    @Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "grp2");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); //earliest
        return props;
    }

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs());
    }

    //This is needed if we want to ThreadPoolExecutor for consumer
    @Bean
    public AsyncListenableTaskExecutor asyncListenableTaskExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        return executor;
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.getContainerProperties().setPollTimeout(10000);//ms
        //For executor
        //factory.setConcurrency(10);
        //factory.getContainerProperties().setConsumerTaskExecutor(asyncListenableTaskExecutor());
        return factory;
    }

    //This is needed for headers and Jackson lib is needed in build.gradle for this
    @Bean
    public DefaultKafkaHeaderMapper headerMapper(){
        return new DefaultKafkaHeaderMapper();
    }

}
