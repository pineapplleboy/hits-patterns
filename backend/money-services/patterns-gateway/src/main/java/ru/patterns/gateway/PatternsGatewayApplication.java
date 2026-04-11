package ru.patterns.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "ru.patterns")
@EnableScheduling
public class PatternsGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(PatternsGatewayApplication.class, args);
    }
}
