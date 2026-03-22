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
import ru.patterns.transfers.application.common.client.CalculatorRequestModel;
import ru.patterns.transfers.application.kafka.provider.TransferAssignmentProvider;

import java.math.BigDecimal;
import java.util.Objects;

@Slf4j
@Service
public class TransferRequestService {

    private final TransferAssignmentProvider transferAssignmentProvider;
    private final RestClient currencyClient;

    @Value("${accounts.master-account-number}")
    private String masterAccountNumber;

    public TransferRequestService(@Qualifier("currencyClient") RestClient currencyClient, TransferAssignmentProvider transferAssignmentProvider) {
        this.currencyClient = currencyClient;
        this.transferAssignmentProvider = transferAssignmentProvider;
    }

    public void processTransferRequest(TransferRequestMessage message, String token) {
        transferAssignmentProvider.send(enrichTransfer(message, token), token);
    }

    public void processReject(TransferRequestMessage message, String token) {
        TransferAssignmentMessage assignment = TransferMessageFactory.createAssignment(message);
        assignment.setStatus(OperationStatus.REJECTED);

        transferAssignmentProvider.send(enrichTransfer(message, token), token);
    }

    private TransferAssignmentMessage enrichTransfer(TransferRequestMessage message, String token) {
        TransferAssignmentMessage assignment = TransferMessageFactory.createAssignment(message);
        boolean isReplenishment = message.getAccountNumberFrom() == null;
        boolean isWithdrawal = message.getAccountNumberTo() == null;

        if (assignment.getRepeatAmount() > 0) {
            return assignment;
        }

        if (Objects.equals(message.getAccountNumberFrom(), message.getAccountNumberTo())
                && message.getAccountNumberFrom() != null) {
            assignment.setStatus(OperationStatus.REJECTED);
            return assignment;
        }

        if (isWithdrawal) {
            assignment.setAmountTo(calculateCurrency(message.getCurrencyFrom(), CurrencyConstants.BASE_CURRENCY_ID, message.getAmount(), token)
                    .getAmountFinal());
        }

        if (isWithdrawal) {
            assignment.setAccountNumberTo(masterAccountNumber);
        } else {
            assignment.setAccountNumberTo(message.getAccountNumberTo());
        }

        if (isReplenishment) {
            assignment.setAmountFrom(calculateCurrency(message.getCurrencyTo(), CurrencyConstants.BASE_CURRENCY_ID, message.getAmount(), token)
                    .getAmountFinal());
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
            return assignment;
        }

        if (message.getCurrencyFrom().equals(message.getCurrencyTo())) {
            assignment.setAmountTo(message.getAmount());
        } else {

            try {
                assignment.setAmountTo(calculateAmountTo(message.getCurrencyFrom(), message.getCurrencyTo(), message.getAmount(), token));
            }
            catch (Exception e) {
                log.error("Ошибка при обогащении запроса на перевод", e);
                assignment.setStatus(OperationStatus.REJECTED);
            }
        }

        return assignment;
    }

    private BigDecimal calculateAmountTo(Integer currencyFrom, Integer currencyTo, BigDecimal amount, String token) {
        return calculateCurrency(currencyFrom, currencyTo, amount, token).getAmountFinal();
    }

    private CurrencyAmountModel calculateCurrency(Integer currencyFrom, Integer currencyTo, BigDecimal amount, String token) {
        return currencyClient.post()
                .uri("/calculate")
                .body(new CalculatorRequestModel(currencyFrom, currencyTo, amount))
                .header("Authorization", token)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }
}
