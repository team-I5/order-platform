package com.spartaclub.orderplatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableCaching
@EnableJpaAuditing(auditorAwareRef = "auditorProvider") // Spring Data JPA Auditing 기능 활성화
public class OrderPlatformApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderPlatformApplication.class, args);
	}

}
