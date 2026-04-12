package ru.patterns.shared.utility;

import lombok.experimental.UtilityClass;
import ru.patterns.shared.constants.ErrorMessages;
import ru.patterns.shared.exception.ForbiddenException;
import ru.patterns.shared.model.external.AuthUser;
import ru.patterns.shared.model.external.Role;

import java.util.UUID;

@UtilityClass
public class AuthUtility {

    public void isAuthorized(String authorizationHeader) {
        AuthUser authUser = JwtAuthUtility.parseAuthorizationHeader(authorizationHeader);

        if (authUser.role() == null || authUser.userId() == null) {
            throw new ForbiddenException(ErrorMessages.FORBIDDEN, null);
        }
    }

    public void checkUserRights(AuthUser authUser, UUID userId) {
        if (!(authUser.role() == Role.EMPLOYEE || authUser.userId().equals(userId))) {
            throw new ForbiddenException(ErrorMessages.FORBIDDEN, null);
        }
    }

    public void checkUserIdEquality(String authorizationHeader, UUID userId) {
        AuthUser authUser = JwtAuthUtility.parseAuthorizationHeader(authorizationHeader);
        if (!authUser.userId().equals(userId)) {
            throw new ForbiddenException(ErrorMessages.FORBIDDEN, null);
        }
    }

    public void checkUserIfEmployee(String authorizationHeader) {
        AuthUser authUser = JwtAuthUtility.parseAuthorizationHeader(authorizationHeader);
        if (authUser.role() != Role.EMPLOYEE) {
            throw new ForbiddenException(ErrorMessages.FORBIDDEN, null);
        }
    }

    public void checkUserIdEqualityOrUserEmployee(String authorizationHeader, UUID userId) {
        AuthUser authUser = JwtAuthUtility.parseAuthorizationHeader(authorizationHeader);
        AuthUtility.checkUserRights(authUser, userId);
    }
}
