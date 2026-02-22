package ru.patterns.shared.utility;

import lombok.experimental.UtilityClass;
import ru.patterns.shared.constants.ErrorMessages;
import ru.patterns.shared.exception.ForbiddenException;
import ru.patterns.shared.model.external.AuthUser;
import ru.patterns.shared.model.external.Role;

import java.util.UUID;

@UtilityClass
public class AuthUtility {

    public void checkUserRights(AuthUser authUser, UUID userId) {
        if (!(authUser.role() == Role.EMPLOYEE || authUser.userId().equals(userId))) {
            throw new ForbiddenException(ErrorMessages.FORBIDDEN);
        }
    }
}
