package ru.patterns.account.application.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import org.springframework.stereotype.Service;
import ru.patterns.account.application.common.model.BankAccountShortModel;
import ru.patterns.account.domain.mapper.BankAccountMapper;
import ru.patterns.account.domain.repository.BankAccountRepository;
import ru.patterns.shared.constants.ErrorMessages;
import ru.patterns.shared.exception.ForbiddenException;
import ru.patterns.shared.model.external.AuthUser;
import ru.patterns.shared.model.external.Role;

import java.util.List;

@Service
@RequiredArgsConstructor
@ExtensionMethod(BankAccountMapper.class)
public class BankAccountService {

    private final BankAccountRepository bankAccountRepository;

    public List<BankAccountShortModel> getAllBankAccounts(AuthUser authUser) {
        if (authUser.role() != Role.EMPLOYEE) {
            throw new ForbiddenException(ErrorMessages.FORBIDDEN);
        }

        return bankAccountRepository.findAll().stream()
                .map(account -> account.toShortModel())
                .toList();
    }
}
