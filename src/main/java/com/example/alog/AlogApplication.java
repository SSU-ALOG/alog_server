package com.example.alog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AlogApplication {

	public static void main(String[] args) {
		SpringApplication.run(AlogApplication.class, args);
	}

}
