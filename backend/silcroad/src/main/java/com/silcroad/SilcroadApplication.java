package com.silcroad;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SilcroadApplication {

	public static void main(String[] args) {
		System.setProperty("application","application-db");
		SpringApplication.run(SilcroadApplication.class, args);
	}
}
