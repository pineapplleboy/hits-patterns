package ru.patterns.account.application.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import org.springframework.stereotype.Service;
import ru.patterns.account.application.common.enums.TransferAccountType;
import ru.patterns.account.application.common.model.OperationModel;
import ru.patterns.account.domain.mapper.OperationMapper;
import ru.patterns.account.domain.repository.OperationRepository;
import ru.patterns.shared.constants.ErrorMessages;
import ru.patterns.shared.exception.ForbiddenException;
import ru.patterns.shared.model.external.AuthUser;
import ru.patterns.shared.model.external.Role;
import ru.patterns.shared.utility.AuthUtility;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@ExtensionMethod(OperationMapper.class)
public class OperationService {

    private final OperationRepository operationRepository;

    public List<OperationModel> getUserOperations(AuthUser authUser, UUID userId) {
        AuthUtility.checkUserRights(authUser, userId);

        return operationRepository.findByUserIdFrom(userId).stream()
                .map(operation -> operation.toModel())
                .toList();
    }

    public List<OperationModel> getAccountOperations(AuthUser authUser, UUID userId,
                                                     String accountNumber, TransferAccountType transferAccountType) {
        AuthUtility.checkUserRights(authUser, userId);

        return operationRepository.findByAccountNumberFromAndTransferAccountType(accountNumber, transferAccountType)
                .stream()
                .map(operation -> operation.toModel())
                .toList();
    }
}
