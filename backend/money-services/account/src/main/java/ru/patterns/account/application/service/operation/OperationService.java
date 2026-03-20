package ru.patterns.account.application.service.operation;

import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import org.springframework.stereotype.Service;
import ru.patterns.account.application.common.enums.AccountActionType;
import ru.patterns.shared.constants.ErrorMessages;
import ru.patterns.shared.model.enums.TransferAccountType;
import ru.patterns.account.application.common.model.operation.OperationModel;
import ru.patterns.account.domain.entity.Operation;
import ru.patterns.account.domain.mapper.OperationMapper;
import ru.patterns.account.domain.repository.OperationRepository;
import ru.patterns.shared.exception.NotFoundException;
import ru.patterns.shared.model.response.OperationStatusResponseModel;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@ExtensionMethod(OperationMapper.class)
public class OperationService {

    private final OperationRepository operationRepository;

    public List<OperationModel> getUserOperations(UUID userId) {
        var outgoingOperations = operationRepository.findByUserIdFrom(userId);
        var incomingOperations = operationRepository.findByRecipientId(userId);

        return Stream.concat(outgoingOperations.stream(), incomingOperations.stream())
                .map(operation -> {
                    var accountNumber = operation.getUserIdFrom().equals(userId) ? operation.getAccountNumberFrom() :
                            operation.getRecipientAccountNumber();

                    return operation.getTransferAccountType() == TransferAccountType.CREDIT_ACCOUNT ?
                            OperationMapper.toCreditOperationModel(operation, accountNumber) :
                            OperationMapper.toBankAccountOperationModel(operation, accountNumber);
                }
                )
                .sorted(Comparator.comparing(OperationModel::getCreateTime))
                .toList().reversed();
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
                .collect(java.util.stream.Collectors.toMap(
                        Operation::getOperationId,
                        Function.identity(),
                        (left, right) -> left
                ))
                .values()
                .stream()
                .map(operation -> operation.getTransferAccountType() == TransferAccountType.CREDIT_ACCOUNT ?
                        OperationMapper.toCreditOperationModel(operation, accountNumber) :
                        OperationMapper.toBankAccountOperationModel(operation, accountNumber))
                .sorted(Comparator.comparing(OperationModel::getCreateTime))
                .toList().reversed();
    }

    public List<OperationModel> getExpiredCreditOperations(UUID userId) {
        return operationRepository.findByUserIdFromAndTransferAccountTypeAndActionTypeAndPurchasedFalseAndExpectedPaymentDateBefore(
                        userId,
                        TransferAccountType.CREDIT_ACCOUNT,
                        AccountActionType.CREDIT_DEPT_PERCENT,
                        Instant.now()
                )
                .stream()
                .map(operation -> OperationMapper.toCreditOperationModel(operation, operation.getAccountNumberFrom()))
                .sorted(Comparator.comparing(OperationModel::getCreateTime))
                .toList();
    }

    private Stream<Operation> getOperationsByType(String accountNumber, TransferAccountType transferAccountType) {
        var outgoingOperations = operationRepository
                .findByAccountNumberFromAndTransferAccountType(accountNumber, transferAccountType);

        var incomingOperations = operationRepository
                .findByRecipientAccountNumberAndTransferAccountType(accountNumber, transferAccountType);

        return Stream.concat(outgoingOperations.stream(), incomingOperations.stream());
    }

    public OperationStatusResponseModel getOperationStatus(UUID operationId) {
        return new OperationStatusResponseModel(getOperationById(operationId).getStatus());
    }

    private Operation getOperationById(UUID operationId) {
        return operationRepository.findById(operationId)
                .orElseThrow(() -> new NotFoundException(ErrorMessages.OPERATION_NOT_FOUND));
    }
}
