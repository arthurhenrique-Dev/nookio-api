package com.henrique.nookio_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class NookioApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(NookioApiApplication.class, args);
	}

}
