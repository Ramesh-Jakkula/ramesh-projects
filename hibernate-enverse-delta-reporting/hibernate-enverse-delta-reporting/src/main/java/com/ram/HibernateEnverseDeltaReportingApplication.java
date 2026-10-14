package com.ram;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
@EnableJpaAuditing
@SpringBootApplication
public class HibernateEnverseDeltaReportingApplication {

	public static void main(String[] args) {
		SpringApplication.run(HibernateEnverseDeltaReportingApplication.class, args);
	}

}
