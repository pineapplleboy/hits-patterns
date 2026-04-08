package ru.patterns.credit.application.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import ru.patterns.credit.application.common.model.account.CreditAccountHistoryModel;
import ru.patterns.credit.application.common.model.operation.CreditOperationModel;
import ru.patterns.credit.application.common.model.operation.CreditStats;
import ru.patterns.credit.application.common.model.response.CreditRatingModel;
import ru.patterns.shared.model.log.TracingLog;
import ru.patterns.shared.monitoring.logger.MonitoringLogger;
import ru.patterns.shared.utility.RestClientRetryUtility;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class CreditRatingService {

    private final RestClient accountClient;
    private final MonitoringLogger monitoringLogger;

    public CreditRatingService(@Qualifier("accountClient") RestClient accountClient,
                               MonitoringLogger monitoringLogger) {
        this.accountClient = accountClient;
        this.monitoringLogger = monitoringLogger;
    }

    // Рейтинг формируется автоматически с учетом платежной дисциплины, долговой нагрузки,
    // количества заявок на кредиты и срока кредитной истории

    public CreditRatingModel getUserCreditRating(UUID userId, String token, TracingLog logData) {
        monitoringLogger.logInfo(logData, "Получен запрос на расчёт кредитного рейтинга пользователя");

        var creditHistory = getUserCreditHistory(userId, token);
        if (creditHistory == null || creditHistory.isEmpty()) {
            monitoringLogger.logWarn(logData, "Кредитная история пользователя пуста, будет использован базовый рейтинг");
        }
        var stats = collectCreditStats(creditHistory);

        var creditRating = calculateCreditRating(stats);

        return new CreditRatingModel()
                .setRating(creditRating)
                .setTotalCreditCounter(stats.getTotalCreditCounter())
                .setClosedCreditCounter(stats.getClosedCreditCounter())
                .setActiveCreditAmount(stats.getActiveCreditAmount())
                .setExpiredOperationsAmount(stats.getExpiredCreditAmount());
    }

    private int calculateCreditRating(CreditStats stats) {
        if (stats.getTotalCreditCounter() == 0) {
            return 650;
        }

        long creditRating = 650;

        creditRating += Math.min(90, stats.getTotalCreditCounter() * 15L);
        creditRating += Math.min(120, stats.getClosedCreditCounter() * 35L);
        creditRating -= Math.min(160, stats.getActiveCreditAmount() * 10L);
        creditRating -= Math.min(750, stats.getExpiredCreditAmount() * 80L);
        creditRating -= calculateDebtPenalty(stats.getTotalCurrentDebt());

        return (int) Math.max(1, Math.min(999, creditRating));
    }

    private List<CreditAccountHistoryModel> getUserCreditHistory(UUID userId, String token) {
        return RestClientRetryUtility.execute(() -> accountClient.get()
                .uri("/{userId}/credit-accounts/history", userId)
                .header("Authorization", token)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                }));
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

    private CreditStats collectCreditStats(List<CreditAccountHistoryModel> creditHistory) {
        if (creditHistory == null || creditHistory.isEmpty()) {
            return new CreditStats();
        }

        long totalCreditCounter = 0;
        long closedCreditCounter = 0;
        long activeCreditAmount = 0;
        long expiredCreditAmount = 0;
        BigDecimal totalCurrentDebt = BigDecimal.ZERO;

        for (CreditAccountHistoryModel credit : creditHistory) {
            totalCreditCounter++;

            BigDecimal currentDebt = parseAmount(credit.getDept());
            totalCurrentDebt = totalCurrentDebt.add(currentDebt);

            if (currentDebt.compareTo(BigDecimal.ZERO) == 0) {
                closedCreditCounter++;
            } else {
                activeCreditAmount++;
            }

            List<CreditOperationModel> operations = credit.getOperations();
            if (operations == null || operations.isEmpty()) {
                continue;
            }

            for (CreditOperationModel operation : operations) {
                if (operation.isExpired()) {
                    expiredCreditAmount++;
                }
            }
        }

        return new CreditStats(
                totalCreditCounter,
                closedCreditCounter,
                activeCreditAmount,
                expiredCreditAmount,
                totalCurrentDebt
        );
    }

}
