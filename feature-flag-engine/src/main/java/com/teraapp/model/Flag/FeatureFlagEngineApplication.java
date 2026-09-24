package com.teraapp.model.Flag;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication(scanBasePackages = "com.teraapp")
@EnableKafka
public class FeatureFlagEngineApplication {
	public static void main(String[] args) {
		SpringApplication.run(FeatureFlagEngineApplication.class, args);
	}
}