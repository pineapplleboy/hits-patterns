package ru.patterns.account.application.service.operation;

import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import org.springframework.stereotype.Service;
import ru.patterns.account.application.common.enums.AccountActionType;
import ru.patterns.account.application.common.model.operation.CreditOperationModel;
import ru.patterns.account.application.common.model.operation.OperationModel;
import ru.patterns.account.domain.entity.Operation;
import ru.patterns.account.domain.mapper.OperationMapper;
import ru.patterns.account.domain.repository.OperationRepository;
import ru.patterns.shared.constants.ErrorMessages;
import ru.patterns.shared.exception.NotFoundException;
import ru.patterns.shared.model.enums.TransferAccountType;
import ru.patterns.shared.model.log.TracingLog;
import ru.patterns.shared.model.response.OperationStatusResponseModel;
import ru.patterns.shared.monitoring.logger.MonitoringLogger;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@ExtensionMethod(OperationMapper.class)
public class OperationService {

    private final OperationRepository operationRepository;
    private final MonitoringLogger monitoringLogger;

    public List<OperationModel> getUserOperations(UUID userId) {
        var outgoingOperations = operationRepository.findByUserIdFrom(userId);
        var incomingOperations = operationRepository.findByRecipientId(userId);

        return Stream.concat(outgoingOperations.stream(), incomingOperations.stream())
                .map(operation -> {
                            var accountNumber = Objects.equals(operation.getUserIdFrom(), userId) ? operation.getAccountNumberFrom() :
                                    operation.getRecipientAccountNumber();

                            return mapOperation(operation, accountNumber);
                        }
                )
                .sorted(Comparator.comparing(OperationModel::getCreateTime))
                .toList().reversed();
    }

    public List<OperationModel> getUserOperations(UUID userId, TracingLog logData) {
        monitoringLogger.logInfo(logData, "Получен запрос на получение операций пользователя");

        return getUserOperations(userId);
    }

    public List<OperationModel> getAccountOperations(String accountNumber, TransferAccountType transferAccountType) {
        Stream<Operation> operations = getOperationsByType(accountNumber, transferAccountType);
        if (transferAccountType == TransferAccountType.BANK_ACCOUNT) {
            operations = Stream.concat(
                    operations,
                    getOperationsByType(accountNumber, TransferAccountType.CREDIT_ACCOUNT)
            );
        }

        return operations
                .collect(Collectors.toMap(
                        Operation::getOperationId,
                        Function.identity(),
                        (left, right) -> left
                ))
                .values()
                .stream()
                .map(operation -> mapOperation(operation, accountNumber))
                .sorted(Comparator.comparing(OperationModel::getCreateTime))
                .toList()
                .reversed();
    }

    public List<OperationModel> getAccountOperations(String accountNumber, TransferAccountType transferAccountType, TracingLog logData) {
        monitoringLogger.logInfo(logData, "Получен запрос на получение операций по счёту");

        return getAccountOperations(accountNumber, transferAccountType);
    }

    public Map<String, List<OperationModel>> getAccountOperations(Set<String> accountNumbers, TransferAccountType transferAccountType) {
        if (accountNumbers == null || accountNumbers.isEmpty()) {
            return Map.of();
        }

        LinkedHashMap<UUID, Operation> uniqueOperations = Stream.concat(
                        operationRepository.findByAccountNumberFromInAndTransferAccountType(accountNumbers, transferAccountType).stream(),
                        operationRepository.findByRecipientAccountNumberInAndTransferAccountType(accountNumbers, transferAccountType).stream()
                )
                .collect(Collectors.toMap(
                        Operation::getOperationId,
                        Function.identity(),
                        (left, right) -> left,
                        LinkedHashMap::new
                ));

        return uniqueOperations.values().stream()
                .collect(Collectors.groupingBy(
                        operation -> resolveAccountNumber(operation, accountNumbers),
                        LinkedHashMap::new,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                operations -> operations.stream()
                                        .map(operation -> mapOperation(operation, resolveAccountNumber(operation, accountNumbers)))
                                        .sorted(Comparator.comparing(OperationModel::getCreateTime).reversed())
                                        .toList()
                        )
                ));
    }

    public List<OperationModel> getExpiredCreditOperations(UUID userId) {
        return operationRepository.findByUserIdFromAndTransferAccountTypeAndActionType(
                        userId,
                        TransferAccountType.CREDIT_ACCOUNT,
                        AccountActionType.CREDIT_DEPT_PERCENT
                )
                .stream()
                .map(operation -> OperationMapper.toCreditOperationModel(operation, operation.getAccountNumberFrom()))
                .map(CreditOperationModel.class::cast)
                .filter(CreditOperationModel::isExpired)
                .map(OperationModel.class::cast)
                .sorted(Comparator.comparing(OperationModel::getCreateTime))
                .toList();
    }

    public List<OperationModel> getExpiredCreditOperations(UUID userId, TracingLog logData) {
        monitoringLogger.logInfo(logData, "Получен запрос на получение просроченных кредитных операций");

        return getExpiredCreditOperations(userId);
    }

    private Stream<Operation> getOperationsByType(String accountNumber, TransferAccountType transferAccountType) {
        var outgoingOperations = operationRepository
                .findByAccountNumberFromAndTransferAccountType(accountNumber, transferAccountType);

        var incomingOperations = operationRepository
                .findByRecipientAccountNumberAndTransferAccountType(accountNumber, transferAccountType);

        return Stream.concat(outgoingOperations.stream(), incomingOperations.stream());
    }

    private OperationModel mapOperation(Operation operation, String accountNumber) {
        return operation.getTransferAccountType() == TransferAccountType.CREDIT_ACCOUNT ?
                OperationMapper.toCreditOperationModel(operation, accountNumber) :
                OperationMapper.toBankAccountOperationModel(operation, accountNumber);
    }

    private String resolveAccountNumber(Operation operation, Set<String> accountNumbers) {
        if (accountNumbers.contains(operation.getAccountNumberFrom())) {
            return operation.getAccountNumberFrom();
        }

        if (accountNumbers.contains(operation.getRecipientAccountNumber())) {
            return operation.getRecipientAccountNumber();
        }

        return operation.getAccountNumberFrom();
    }

    public OperationStatusResponseModel getOperationStatus(UUID operationId) {
        return new OperationStatusResponseModel(getOperationById(operationId).getStatus());
    }

    public OperationStatusResponseModel getOperationStatus(UUID operationId, TracingLog logData) {
        monitoringLogger.logInfo(logData, "Получен запрос на получение статуса операции");

        return getOperationStatus(operationId);
    }

    private Operation getOperationById(UUID operationId) {
        return operationRepository.findById(operationId)
                .orElseThrow(() -> new NotFoundException(ErrorMessages.OPERATION_NOT_FOUND, null));
    }
}
