package ru.patterns.transfers.application.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import ru.patterns.shared.constants.CurrencyConstants;
import ru.patterns.shared.factory.TransferMessageFactory;
import ru.patterns.shared.model.client.CurrencyAmountModel;
import ru.patterns.shared.model.enums.OperationStatus;
import ru.patterns.shared.model.kafka.TransferAssignmentMessage;
import ru.patterns.shared.model.kafka.TransferRequestMessage;
import ru.patterns.shared.model.log.TracingLog;
import ru.patterns.shared.monitoring.logger.MonitoringLogger;
import ru.patterns.shared.utility.RestClientRetryUtility;
import ru.patterns.transfers.application.common.client.CalculatorRequestModel;
import ru.patterns.transfers.application.kafka.provider.TransferAssignmentProvider;

import java.math.BigDecimal;
import java.util.Objects;

@Slf4j
@Service
public class TransferRequestService {

    private final TransferAssignmentProvider transferAssignmentProvider;
    private final RestClient currencyClient;
    private final MonitoringLogger monitoringLogger;

    @Value("${accounts.master-account-number}")
    private String masterAccountNumber;

    @Value("${service.name}")
    private String serviceName;

    public TransferRequestService(@Qualifier("currencyClient") RestClient currencyClient,
                                  TransferAssignmentProvider transferAssignmentProvider,
                                  MonitoringLogger monitoringLogger) {
        this.currencyClient = currencyClient;
        this.transferAssignmentProvider = transferAssignmentProvider;
        this.monitoringLogger = monitoringLogger;
    }

    public void processTransferRequest(TransferRequestMessage message, String token, String traceId) {
        var logData = createLogData(traceId);
        monitoringLogger.logInfo(logData, "Получен запрос на обработку перевода");

        transferAssignmentProvider.send(enrichTransfer(message, token, logData), token, traceId);
    }

    public void processReject(TransferRequestMessage message, String token, String traceId) {
        var logData = createLogData(traceId);
        monitoringLogger.logWarn(logData, "Запрос на перевод будет отклонён");

        TransferAssignmentMessage assignment = TransferMessageFactory.createAssignment(message);
        assignment.setStatus(OperationStatus.REJECTED);

        transferAssignmentProvider.send(assignment, token, traceId);
    }

    private TransferAssignmentMessage enrichTransfer(TransferRequestMessage message, String token, TracingLog logData) {
        TransferAssignmentMessage assignment = TransferMessageFactory.createAssignment(message);
        boolean isReplenishment = message.getAccountNumberFrom() == null;
        boolean isWithdrawal = message.getAccountNumberTo() == null;

        if (assignment.getRepeatAmount() > 0) {
            monitoringLogger.logWarn(logData, "Повторная обработка запроса на перевод");
            return assignment;
        }

        if (Objects.equals(message.getAccountNumberFrom(), message.getAccountNumberTo())
                && message.getAccountNumberFrom() != null) {
            monitoringLogger.logWarn(logData, "Попытка перевода на тот же самый счёт");
            assignment.setStatus(OperationStatus.REJECTED);
            return assignment;
        }

        if (isWithdrawal) {
            assignment.setAmountTo(calculateCurrency(
                    message.getCurrencyFrom(),
                    CurrencyConstants.BASE_CURRENCY_ID,
                    message.getAmount(),
                    token,
                    logData
            ).getAmountFinal());
        }

        if (isWithdrawal) {
            assignment.setAccountNumberTo(masterAccountNumber);
        } else {
            assignment.setAccountNumberTo(message.getAccountNumberTo());
        }

        if (isReplenishment) {
            assignment.setAmountFrom(calculateCurrency(
                    message.getCurrencyTo(),
                    CurrencyConstants.BASE_CURRENCY_ID,
                    message.getAmount(),
                    token,
                    logData
            ).getAmountFinal());
        } else {
            assignment.setAmountFrom(message.getAmount());
        }

        if (isReplenishment) {
            assignment.setAccountNumberFrom(masterAccountNumber);
        } else {
            assignment.setAccountNumberFrom(message.getAccountNumberFrom());
        }

        if (isReplenishment) {
            assignment.setAmountTo(message.getAmount());
            monitoringLogger.logInfo(logData, "Запрос на пополнение успешно обогащён");
            return assignment;
        }

        if (message.getCurrencyFrom().equals(message.getCurrencyTo())) {
            assignment.setAmountTo(message.getAmount());
            monitoringLogger.logInfo(logData, "Запрос на перевод без конвертации успешно подготовлен");
        } else {
            try {
                assignment.setAmountTo(calculateAmountTo(message.getCurrencyFrom(), message.getCurrencyTo(), message.getAmount(), token, logData));
                monitoringLogger.logInfo(logData, "Запрос на перевод с конвертацией успешно подготовлен");
            } catch (Exception exception) {
                monitoringLogger.logError(logData, "Ошибка при обогащении запроса на перевод");
                log.error("Ошибка при обогащении запроса на перевод", exception);
                assignment.setStatus(OperationStatus.REJECTED);
            }
        }

        return assignment;
    }

    private BigDecimal calculateAmountTo(Integer currencyFrom, Integer currencyTo, BigDecimal amount, String token, TracingLog logData) {
        return calculateCurrency(currencyFrom, currencyTo, amount, token, logData).getAmountFinal();
    }

    private CurrencyAmountModel calculateCurrency(Integer currencyFrom, Integer currencyTo, BigDecimal amount, String token, TracingLog logData) {
        monitoringLogger.logInfo(logData, "Получен запрос на расчёт суммы перевода");

        return RestClientRetryUtility.execute(() -> currencyClient.post()
                .uri("/calculate")
                .body(new CalculatorRequestModel(currencyFrom, currencyTo, amount))
                .header("Authorization", token)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {}));
    }

    private TracingLog createLogData(String traceId) {
        return new TracingLog()
                .setTraceId(traceId == null ? "" : traceId)
                .setAuthorization(null)
                .setServiceId(serviceName);
    }
}
