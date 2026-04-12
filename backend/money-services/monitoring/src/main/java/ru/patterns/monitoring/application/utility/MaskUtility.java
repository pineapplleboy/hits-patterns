package ru.patterns.monitoring.application.utility;

import lombok.experimental.UtilityClass;

import java.util.regex.Pattern;

@UtilityClass
public class MaskUtility {

    private static final int MAX_LOG_BODY_LENGTH = 4096;
    private static final Pattern SENSITIVE_AMOUNT_OR_BALANCE_PATTERN = Pattern.compile(
            "(?i)(\"[^\"]*(?:amount|balance|access_token)[^\"]*\"\\s*:\\s*)(\"[^\"]*\"|-?\\d+(?:\\.\\d+)?)"
    );

    public String maskAuthorization(String authorization) {
        return authorization != null ? authorization.split(" ")[0] + " ***" : "-";
    }

    public String maskBody(String path, String body) {
        if (path != null && (path.contains("/swagger-ui") || path.contains("api-docs") || path.contains("token"))) {
            return "*";
        }

        if (body == null || body.isBlank()) {
            return body;
        }

        var maskedBody = SENSITIVE_AMOUNT_OR_BALANCE_PATTERN.matcher(body).replaceAll("$1\"*\"");
        if (maskedBody.length() <= MAX_LOG_BODY_LENGTH) {
            return maskedBody;
        }

        return maskedBody.substring(0, MAX_LOG_BODY_LENGTH) + "...[truncated]";
    }
}
