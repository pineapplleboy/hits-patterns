package ru.patterns.account.application.service.operation;

import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import org.springframework.stereotype.Service;
import ru.patterns.shared.model.enums.TransferAccountType;
import ru.patterns.account.application.common.model.OperationModel;
import ru.patterns.account.domain.entity.Operation;
import ru.patterns.account.domain.mapper.OperationMapper;
import ru.patterns.account.domain.repository.OperationRepository;
import ru.patterns.shared.exception.NotFoundException;
import ru.patterns.shared.model.response.OperationStatusResponseModel;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
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
                .map(operation -> OperationMapper.toModel(operation, userId))
                .sorted(Comparator.comparing(OperationModel::getCreateTime))
                .toList().reversed();
    }

    public List<OperationModel> getAccountOperations(UUID userId,
                                                     String accountNumber, TransferAccountType transferAccountType) {
        var outgoingOperations = operationRepository
                .findByAccountNumberFromAndTransferAccountTypeAndUserIdFrom(accountNumber, transferAccountType, userId);

        var incomingOperations = operationRepository
                .findByRecipientAccountNumberAndTransferAccountTypeAndRecipientId(accountNumber, transferAccountType, userId);

        return Stream.concat(outgoingOperations.stream(), incomingOperations.stream())
                .map(operation -> OperationMapper.toModel(operation, userId))
                .sorted(Comparator.comparing(OperationModel::getCreateTime))
                .toList().reversed();
    }

    public OperationModel getOperationInfo(UUID operationId) {
        return getOperationById(operationId).toModel();
    }

    public OperationStatusResponseModel getOperationStatus(UUID operationId) {
        return new OperationStatusResponseModel(getOperationById(operationId).getStatus());
    }

    private Operation getOperationById(UUID operationId) {
        return operationRepository.findById(operationId)
                .orElseThrow(() -> new NotFoundException("Operation not found!"));
    }
}
