package ru.patterns.account.application.service.currency;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import ru.patterns.account.application.common.model.currency.CurrencyModel;

@Service
public class CurrencyService {

    private final RestClient currencyClient;

    public CurrencyService(@Qualifier("currencyClient") RestClient currencyClient) {
        this.currencyClient = currencyClient;
    }

    public CurrencyModel getCurrencyById(Integer currencyId, String token) {
        try {
            return retrieveCurrency(currencyId, token);
        }
        catch (Exception ignored) {
            return null;
        }
    }

    private CurrencyModel retrieveCurrency(Integer currencyId, String token) {
        return currencyClient.get()
                .uri("/all-rates/{currencyId}", currencyId)
                .header("Authorization", token)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }
}
