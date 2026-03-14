package ru.patterns.currency;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import ru.patterns.currency.application.config.CurrencyConfig;

@SpringBootApplication
@EnableConfigurationProperties(CurrencyConfig.class)
public class CurrencyApplication {

	public static void main(String[] args) {
		SpringApplication.run(CurrencyApplication.class, args);
	}

}
