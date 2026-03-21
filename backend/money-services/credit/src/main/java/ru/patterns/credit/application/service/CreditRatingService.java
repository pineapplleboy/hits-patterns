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

        var totalCreditCounter = creditHistory.size();

        var closedCreditCounter = creditHistory.stream()
                .filter(credit -> isZeroDept(credit.getDept()))
                .count();

        var activeCreditAmount = creditHistory.stream()
                .filter(credit -> !isZeroDept(credit.getDept()))
                .count();

        var expiredCreditAmount = creditHistory.stream()
                .map(CreditAccountHistoryModel::getOperations)
                .filter(operations -> operations != null && !operations.isEmpty())
                .mapToLong(this::calculateExpiredCreditAmount)
                .sum();

        var creditRating = calculateCreditRating(creditHistory, totalCreditCounter,
                closedCreditCounter, activeCreditAmount, expiredCreditAmount);

        return new CreditRatingModel()
                .setRating(creditRating)
                .setTotalCreditCounter(totalCreditCounter)
                .setClosedCreditCounter(closedCreditCounter)
                .setActiveCreditAmount(activeCreditAmount)
                .setExpiredOperationsAmount(expiredCreditAmount);
    }

    private long calculateCreditRating(List<CreditAccountHistoryModel> creditHistory,
                                         int totalCreditCounter,
                                         long closedCreditCounter,
                                         long activeCreditAmount,
                                         long expiredCreditAmount) {
        if (creditHistory == null || creditHistory.isEmpty()) {
            return 650;
        }

        long creditRating = 650;

        creditRating += Math.min(90, totalCreditCounter * 15);
        creditRating += (int) Math.min(120, closedCreditCounter * 35);
        creditRating -= (int) Math.min(160, activeCreditAmount * 10);
        creditRating -= (int) Math.min(250, expiredCreditAmount * 45);
        creditRating -= calculateDebtPenalty(calculateSumDept(creditHistory));

        return Math.max(1, Math.min(999, creditRating));
    }

    private List<CreditAccountHistoryModel> getUserCreditHistory(UUID userId, String token) {
        return accountClient.get()
                .uri("/{userId}/credit-accounts/history", userId)
                .header("Authorization", token)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    private BigDecimal calculateSumDept(List<CreditAccountHistoryModel> creditHistory) {
        return creditHistory.stream()
                .map(CreditAccountHistoryModel::getDept)
                .map(this::parseAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private int calculateDebtPenalty(BigDecimal totalCurrentDebt) {
        if (totalCurrentDebt.compareTo(BigDecimal.ZERO) <= 0) {
            return -20;
        }

        if (totalCurrentDebt.compareTo(BigDecimal.valueOf(10_000)) <= 0) {
            return 20;
        }

        if (totalCurrentDebt.compareTo(BigDecimal.valueOf(50_000)) <= 0) {
            return 60;
        }

        if (totalCurrentDebt.compareTo(BigDecimal.valueOf(150_000)) <= 0) {
            return 110;
        }

        return 170;
    }

    private long calculateExpiredCreditAmount(List<CreditOperationModel> operations) {
        return operations.stream()
                .filter(CreditOperationModel::isExpired)
                .count();
    }

    private BigDecimal parseAmount(String amount) {
        if (amount == null) {
            return BigDecimal.ZERO;
        }

        String numericAmount = amount.replaceAll("[^\\d,.-]", "").replace(',', '.');
        if (numericAmount.isBlank()) {
            return BigDecimal.ZERO;
        }

        return new BigDecimal(numericAmount);
    }

    private boolean isZeroDept(String dept) {
        String numericDept = dept.replaceAll("[^\\d,.-]", "").replace(',', '.');
        if (numericDept.isBlank()) {
            return true;
        }

        return BigDecimal.ZERO.compareTo(new BigDecimal(numericDept)) == 0;
    }
}
