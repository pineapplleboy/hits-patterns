package ru.patterns.account.application.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import org.springframework.stereotype.Service;
import ru.patterns.account.application.common.model.OperationModel;
import ru.patterns.account.domain.entity.Operation;
import ru.patterns.account.domain.mapper.OperationMapper;
import ru.patterns.account.domain.repository.OperationRepository;
import ru.patterns.shared.constants.ErrorMessages;
import ru.patterns.shared.exception.ForbiddenException;
import ru.patterns.shared.model.external.AuthUser;
import ru.patterns.shared.model.external.Role;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@ExtensionMethod(OperationMapper.class)
public class OperationService {

    private final OperationRepository operationRepository;

    public List<OperationModel> getUserOperations(AuthUser userAuth, UUID userId) {
        if (!userAuth.userId().equals(userId)) {
            throw new ForbiddenException(ErrorMessages.FORBIDDEN);
        }

        return operationRepository.findByUserIdFrom(userId).stream()
                .map(operation -> operation.toModel())
                .toList();
    }

    public List<OperationModel> getAccountOperations(AuthUser userAuth, UUID userId, String accountNumber) {
        if (!(userAuth.userId().equals(userId) || userAuth.role() != Role.EMPLOYEE)) {
            throw new ForbiddenException(ErrorMessages.FORBIDDEN);
        }

        return operationRepository.findByAccountNumberFrom(accountNumber)
                .stream()
                .map(operation -> operation.toModel())
                .toList();
    }
}
