package com.subham.projects.lovableClone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LovableCloneApplication {

	public static void main(String[] args) {
		SpringApplication.run(LovableCloneApplication.class, args);
	}

}
