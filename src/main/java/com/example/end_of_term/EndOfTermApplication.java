package com.example.end_of_term;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EndOfTermApplication {

    public static void main(String[] args) {
        SpringApplication.run(EndOfTermApplication.class, args);
    }

}
