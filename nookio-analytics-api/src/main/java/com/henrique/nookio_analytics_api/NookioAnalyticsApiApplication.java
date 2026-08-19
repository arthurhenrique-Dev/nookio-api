package com.henrique.nookio_analytics_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class NookioAnalyticsApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(NookioAnalyticsApiApplication.class, args);
	}

}
