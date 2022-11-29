package se.bubbelbubbel.fakenews;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FakenewsApplication {

	public static void main(String[] args) {
		System.out.println("Starting application now");
		SpringApplication.run(FakenewsApplication.class, args);
	}

}
