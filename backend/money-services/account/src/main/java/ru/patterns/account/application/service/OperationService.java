package ru.patterns.account.application.service;

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

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@ExtensionMethod(OperationMapper.class)
public class OperationService {

    private final OperationRepository operationRepository;

    public List<OperationModel> getUserOperations(UUID userId) {
        return operationRepository.findByUserIdFrom(userId).stream()
                .map(operation -> operation.toModel())
                .toList();
    }

    public List<OperationModel> getAccountOperations(UUID userId,
                                                     String accountNumber, TransferAccountType transferAccountType) {
        return operationRepository.findByAccountNumberFromAndTransferAccountType(accountNumber, transferAccountType)
                .stream()
                .map(operation -> operation.toModel())
                .toList();
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
