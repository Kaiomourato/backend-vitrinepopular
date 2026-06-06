package br.edu.ifpi.picos.backend_vp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BackendVpApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendVpApplication.class, args);
	}

}
