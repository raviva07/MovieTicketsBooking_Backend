package com.movieticket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
@EnableScheduling

@SpringBootApplication
public class MovieTicketBookingBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(MovieTicketBookingBackendApplication.class, args);
	}

}
