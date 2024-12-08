package com.example.RealTimeTicketing;

import com.google.gson.Gson;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class RealTimeTicketingApplication {

	public static void main(String[] args) {
		SpringApplication.run(RealTimeTicketingApplication.class, args);
	}

}
