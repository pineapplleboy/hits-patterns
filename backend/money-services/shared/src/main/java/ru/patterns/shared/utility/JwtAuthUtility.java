package ru.patterns.shared.utility;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.experimental.UtilityClass;
import ru.patterns.shared.exception.UnauthorizedException;
import ru.patterns.shared.model.external.AuthUser;
import ru.patterns.shared.model.external.Role;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static ru.patterns.shared.model.constants.ErrorMessages.UNAUTHORIZED;

@UtilityClass
public class JwtAuthUtility {
    private final String ISSUER = "companyEventsServer";
    private final String AUDIENCE = "companyEventsClient";
    private final String KEY = "superKeyPuPuPu_52#Peterburg-eeeAaALike";
    private final String PREFIX = "Bearer ";

    private final SecretKey tokenKey = Keys.hmacShaKeyFor(KEY.getBytes(StandardCharsets.UTF_8));

    public AuthUser parseAuthorizationHeader(String authorizationHeader) {
        String jwt = extractPrefix(authorizationHeader);

        Jws<Claims> jws = Jwts.parser()
                .requireIssuer(ISSUER)
                .requireAudience(AUDIENCE)
                .verifyWith(tokenKey)
                .build()
                .parseSignedClaims(jwt);

        Claims claims = jws.getPayload();

        UUID userId = UUID.fromString(getClaim(claims, "nameid"));
        Role role = mapRole(getClaim(claims, "role"));

        return new AuthUser(userId, role);
    }

    private String extractPrefix(String header) {
        if (header == null || header.isBlank()) {
            throw new UnauthorizedException(UNAUTHORIZED);
        }

        if (!header.regionMatches(true, 0, PREFIX, 0, PREFIX.length())) {
            throw new UnauthorizedException(UNAUTHORIZED);
        }

        String token = header.substring(PREFIX.length()).trim();
        if (token.isEmpty()) {
            throw new UnauthorizedException(UNAUTHORIZED);
        }

        return token;
    }

    private String getClaim(Claims claims, String claimName) {
        var claimValue = claims.get(claimName);
        if (claimValue == null) {
            throw new UnauthorizedException(UNAUTHORIZED);
        }

        String claim = String.valueOf(claimValue).trim();
        if (claim.isEmpty()) {
            throw new UnauthorizedException(UNAUTHORIZED);
        }

        return claim;
    }

    private Role mapRole(String stringRole) {
        String role = stringRole.trim().toLowerCase();

        return switch (role) {
            case "employee" -> Role.EMPLOYEE;
            case "client" -> Role.CLIENT;
            default -> throw new UnauthorizedException(UNAUTHORIZED);
        };
    }
}
