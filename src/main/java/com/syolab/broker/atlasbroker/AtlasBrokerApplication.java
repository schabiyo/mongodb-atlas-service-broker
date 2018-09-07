package com.syolab.broker.atlasbroker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
public class AtlasBrokerApplication {

	public static void main(String[] args) {

		SpringApplication.run(AtlasBrokerApplication.class, args);
	}
}
