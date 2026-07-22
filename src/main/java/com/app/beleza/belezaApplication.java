package com.app.beleza;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class belezaApplication {
	public static void main(String[] args) {
		SpringApplication.run(belezaApplication.class, args);
	}
}
