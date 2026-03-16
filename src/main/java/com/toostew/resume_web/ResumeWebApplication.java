package com.toostew.resume_web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;


@EnableCaching
@SpringBootApplication
public class ResumeWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(ResumeWebApplication.class, args);
	}

}
