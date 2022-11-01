package com.tweats.tweats;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@SpringBootApplication
public class TweatsApplication {

	public static void main(String[] args) {
		SpringApplication.run(TweatsApplication.class, args);
	}

}
