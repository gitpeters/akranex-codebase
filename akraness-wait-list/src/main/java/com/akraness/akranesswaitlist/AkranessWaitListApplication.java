package com.akraness.akranesswaitlist;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class AkranessWaitListApplication {

	public static void main(String[] args) {
		SpringApplication.run(AkranessWaitListApplication.class, args);
	}

}
