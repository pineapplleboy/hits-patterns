package ru.patterns.credit.application.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import ru.patterns.credit.application.common.model.account.CreditAccountHistoryModel;
import ru.patterns.credit.application.common.model.operation.CreditOperationModel;
import ru.patterns.credit.application.common.model.response.CreditRatingModel;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
public class CreditRatingService {

    private final RestClient accountClient;

    public CreditRatingService(@Qualifier("accountClient") RestClient accountClient) {
        this.accountClient = accountClient;
    }

    // Рейтинг формируется автоматически с учетом платежной дисциплины, долговой нагрузки,
    // количества заявок на кредиты и срока кредитной истории

    public CreditRatingModel getUserCreditRating(UUID userId, String token) {
        var creditHistory = getUserCreditHistory(userId, token);

        return new CreditRatingModel(calculateCreditRating(creditHistory));
    }

    private List<CreditAccountHistoryModel> getUserCreditHistory(UUID userId, String token) {
        return accountClient.get()
                .uri("/{userId}/credit-accounts/history", userId)
                .header("Authorization", token)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    private int calculateCreditRating(List<CreditAccountHistoryModel> creditHistory) {
        int creditRating = 300;
        Instant now = Instant.now();
        Instant monthAgo = now.minus(30, ChronoUnit.DAYS);

        for (CreditAccountHistoryModel history : creditHistory) {

            if (isZeroDept(history.getDept())) {
                creditRating += 50;
            } else {
                creditRating -= 70;
            }

            for (CreditOperationModel operation : history.getOperations()) {
                Instant expectedPaymentDate = operation.getExpectedPaymentDate();

                if (expectedPaymentDate != null && expectedPaymentDate.isBefore(monthAgo)) {
                    creditRating += 30;
                } else if (operation.isExpired() || expectedPaymentDate != null && expectedPaymentDate.isBefore(now)) {
                    creditRating -= 20;
                } else {
                    creditRating += 10;
                }
            }
        }

        return Math.max(1, Math.min(999, creditRating));
    }

    private boolean isZeroDept(String dept) {
        String numericDept = dept.replaceAll("[^\\d,.-]", "").replace(',', '.');
        if (numericDept.isBlank()) {
            return true;
        }

        return BigDecimal.ZERO.compareTo(new BigDecimal(numericDept)) == 0;
    }
}
