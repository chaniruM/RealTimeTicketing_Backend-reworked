package com.example.RealTimeTicketing;

import com.example.RealTimeTicketing.service.TicketPoolService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RealTimeTicketingApplication {

	public static void main(String[] args) {
		SpringApplication.run(RealTimeTicketingApplication.class, args);
	}

}
