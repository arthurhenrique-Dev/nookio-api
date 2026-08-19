package com.henrique.nookio_gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class NookioGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(NookioGatewayApplication.class, args);
	}

}
