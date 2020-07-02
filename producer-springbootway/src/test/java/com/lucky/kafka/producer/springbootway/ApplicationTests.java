package com.lucky.kafka.producer.springbootway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "kafka.publisher.scheduler.enabled=false")
class ApplicationTests {

	@Test
	void contextLoads() {
	}

}
