package ru.patterns.shared.utility;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import lombok.experimental.UtilityClass;
import ru.patterns.shared.constants.ErrorMessages;
import ru.patterns.shared.exception.UnauthorizedException;
import ru.patterns.shared.model.external.AuthUser;
import ru.patterns.shared.model.external.Role;

import java.math.BigInteger;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@UtilityClass
public class JwtAuthUtility {
    private static final String PREFIX = "Bearer ";
    private static final String NAME_ID_CLAIM = "nameid";
    private static final String ROLE_CLAIM = "role";
    private static final String DEFAULT_JWKS_URL = "http://localhost:5001/.well-known/openid-configuration/jwks";
    private static final Duration HTTP_TIMEOUT = Duration.ofSeconds(20);
    private static final Duration KEYS_CACHE_TTL = Duration.ofMinutes(1000);

    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static volatile Map<String, PublicKey> cachedKeys = Map.of();
    private static volatile Instant keysLoadedAt = Instant.EPOCH;

    public AuthUser parseAuthorizationHeader(String authorizationHeader) {
        try {
            String jwt = extractPrefix(authorizationHeader);

            Jws<Claims> jws = Jwts.parser()
                    .keyLocator(header -> resolveVerificationKey(readKeyId(header)))
                    .build()
                    .parseSignedClaims(jwt);

            Claims claims = jws.getPayload();
            validateExpiration(claims);

            UUID userId = UUID.fromString(getClaim(claims, NAME_ID_CLAIM));
            Role role = mapRole(getClaim(claims, ROLE_CLAIM));

            return new AuthUser(userId, role);
        } catch (Exception exception) {
            throw unauthorizedException();
        }
    }

    private Key resolveVerificationKey(String keyId) {
        Map<String, PublicKey> keys = getVerificationKeys(false);

        PublicKey verificationKey = findVerificationKey(keys, keyId);
        if (verificationKey != null) {
            return verificationKey;
        }

        verificationKey = findVerificationKey(getVerificationKeys(true), keyId);
        if (verificationKey == null) {
            throw unauthorizedException();
        }

        return verificationKey;
    }

    private String readKeyId(Header header) {
        Object keyId = header.get("kid");
        return keyId == null ? null : String.valueOf(keyId).trim();
    }

    private Map<String, PublicKey> getVerificationKeys(boolean forceRefresh) {
        if (!forceRefresh && isCacheValid()) {
            return cachedKeys;
        }

        synchronized (JwtAuthUtility.class) {
            if (!forceRefresh && isCacheValid()) {
                return cachedKeys;
            }

            cachedKeys = loadVerificationKeys();
            keysLoadedAt = Instant.now();
            return cachedKeys;
        }
    }

    private Map<String, PublicKey> loadVerificationKeys() {
        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create(resolveJwksUrl()))
                    .GET()
                    .timeout(HTTP_TIMEOUT)
                    .build();

            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            ensureSuccessfulResponse(response);

            JsonNode keysNode = OBJECT_MAPPER.readTree(response.body()).path("keys");
            Map<String, PublicKey> keys = new HashMap<>();
            for (JsonNode keyNode : keysNode) {
                String keyType = keyNode.path("kty").asText();
                String keyId = keyNode.path("kid").asText(null);
                String modulus = keyNode.path("n").asText(null);
                String exponent = keyNode.path("e").asText(null);

                if (!"RSA".equalsIgnoreCase(keyType) || keyId == null || modulus == null || exponent == null) {
                    continue;
                }

                keys.put(keyId, buildRsaPublicKey(modulus, exponent));
            }

            if (keys.isEmpty()) {
                throw unauthorizedException();
            }

            return Map.copyOf(keys);
        } catch (Exception exception) {
            throw unauthorizedException();
        }
    }

    private String resolveJwksUrl() {
        String explicitJwksUrl = System.getenv("AUTH_JWKS_URL");
        if (explicitJwksUrl == null || explicitJwksUrl.isBlank()) {
            return DEFAULT_JWKS_URL;
        }

        return explicitJwksUrl.trim();
    }

    private PublicKey buildRsaPublicKey(String modulus, String exponent) throws Exception {
        BigInteger modulusValue = new BigInteger(1, Base64.getUrlDecoder().decode(modulus));
        BigInteger exponentValue = new BigInteger(1, Base64.getUrlDecoder().decode(exponent));

        RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(modulusValue, exponentValue);
        return KeyFactory.getInstance("RSA").generatePublic(publicKeySpec);
    }

    private boolean isCacheValid() {
        return !cachedKeys.isEmpty() && Instant.now().isBefore(keysLoadedAt.plus(KEYS_CACHE_TTL));
    }

    private PublicKey findVerificationKey(Map<String, PublicKey> keys, String keyId) {
        if (keyId == null) {
            return keys.size() == 1 ? keys.values().iterator().next() : null;
        }

        return keys.get(keyId);
    }

    private void ensureSuccessfulResponse(HttpResponse<String> response) {
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw unauthorizedException();
        }
    }

    private void validateExpiration(Claims claims) {
        Instant expiresTime = claims.getExpiration() != null ? claims.getExpiration().toInstant() : null;
        if (expiresTime == null || expiresTime.isBefore(Instant.now())) {
            throw unauthorizedException();
        }
    }

    private String extractPrefix(String header) {
        if (header == null || header.isBlank()) {
            throw unauthorizedException();
        }

        if (!header.regionMatches(true, 0, PREFIX, 0, PREFIX.length())) {
            throw unauthorizedException();
        }

        String token = header.substring(PREFIX.length()).trim();
        if (token.isEmpty()) {
            throw unauthorizedException();
        }

        return token;
    }

    private String getClaim(Claims claims, String claimName) {
        Object claimValue = claims.get(claimName);
        if (claimValue == null) {
            throw unauthorizedException();
        }

        String claim = String.valueOf(claimValue).trim();
        if (claim.isEmpty()) {
            throw unauthorizedException();
        }

        return claim;
    }

    private Role mapRole(String stringRole) {
        if ("CLIENT".equalsIgnoreCase(stringRole)) {
            return Role.CLIENT;
        }

        if ("EMPLOYEE".equalsIgnoreCase(stringRole)) {
            return Role.EMPLOYEE;
        }

        throw unauthorizedException();
    }

    private UnauthorizedException unauthorizedException() {
        return new UnauthorizedException(ErrorMessages.UNAUTHORIZED);
    }
}
