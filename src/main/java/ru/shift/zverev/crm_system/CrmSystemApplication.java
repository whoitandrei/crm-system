package ru.shift.zverev.crm_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class CrmSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(CrmSystemApplication.class, args);
	}

}
