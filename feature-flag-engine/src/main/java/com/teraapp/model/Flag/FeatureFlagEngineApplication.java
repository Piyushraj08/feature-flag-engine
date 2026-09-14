package com.teraapp.model.Flag;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.teraapp")
public class FeatureFlagEngineApplication {
	public static void main(String[] args) {
		SpringApplication.run(FeatureFlagEngineApplication.class, args);
	}
}