package com.sbspro.midProject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MidProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(MidProjectApplication.class, args);
	}

}
