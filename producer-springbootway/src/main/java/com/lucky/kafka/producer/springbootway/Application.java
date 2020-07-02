package com.lucky.kafka.producer.springbootway;

import com.lucky.kafka.producer.springbootway.service.RawMessagePublisher;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.time.ZonedDateTime;
import java.util.*;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
		/*while(true){
			try {
				Thread.sleep(10000);
				((RawMessagePublisher)context.getBean("rawMessagePublisher")).publishMessage("Sending msg with time " + ZonedDateTime.now());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}*/

	}



}
