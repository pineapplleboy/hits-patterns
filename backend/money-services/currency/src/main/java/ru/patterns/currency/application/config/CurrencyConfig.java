package ru.patterns.currency.application.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import ru.patterns.shared.model.client.ProcessedCurrencyModel;

import java.util.List;

@Data
@ConfigurationProperties(prefix = "currency.list")
public class CurrencyConfig {

    private List<ProcessedCurrencyModel> processed;
}
