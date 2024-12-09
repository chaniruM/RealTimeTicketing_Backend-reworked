package com.example.RealTimeTicketing;

import com.example.RealTimeTicketing.service.TicketPoolService;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class RealTimeTicketingApplication {

	public static void main(String[] args) {
		SpringApplication.run(RealTimeTicketingApplication.class, args);

		Logger logger = LogManager.getLogger(TicketPoolService.class);
		logger.info("This is a test log message.");
	}

}
