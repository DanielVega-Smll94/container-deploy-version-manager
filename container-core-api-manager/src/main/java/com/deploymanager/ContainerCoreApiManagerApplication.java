package com.deploymanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
@EnableScheduling
public class ContainerCoreApiManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ContainerCoreApiManagerApplication.class, args);
	}

}
