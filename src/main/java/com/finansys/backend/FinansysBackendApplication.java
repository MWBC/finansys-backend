package com.finansys.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class FinansysBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(FinansysBackendApplication.class, args);
	}

}
