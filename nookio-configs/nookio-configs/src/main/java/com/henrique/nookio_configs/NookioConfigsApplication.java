package com.henrique.nookio_configs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@EnableConfigServer
@SpringBootApplication
public class NookioConfigsApplication {

	public static void main(String[] args) {
		SpringApplication.run(NookioConfigsApplication.class, args);
	}

}

