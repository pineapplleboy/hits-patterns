package ru.patterns.monitoring.application.utility;

import lombok.experimental.UtilityClass;

import java.util.regex.Pattern;

@UtilityClass
public class MaskUtility {

    private static final Pattern SENSITIVE_AMOUNT_OR_BALANCE_PATTERN = Pattern.compile(
            "(?i)(\"[^\"]*(?:amount|balance|access_token)[^\"]*\"\\s*:\\s*)(\"[^\"]*\"|-?\\d+(?:\\.\\d+)?)"
    );

    public String maskAuthorization(String authorization) {
        return authorization != null ? authorization.split(" ")[0] + " ***" : "-";
    }

    public String maskBody(String path, String body) {
        if (path.contains("/swagger-ui") || path.contains("api-docs")) {
            return "*";
        }

        if (body == null || body.isBlank()) {
            return body;
        }

        return SENSITIVE_AMOUNT_OR_BALANCE_PATTERN.matcher(body).replaceAll("$1\"*\"");
    }
}
