package ru.patterns.gateway.application.common;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.UUID;

@Data
@Accessors(chain = true)
public class IdempotencyPayload {

    private UUID userId;

    private String idempotencyKey;

    private String method;

    private String route;

    private String requestHash;

    private UUID operationId;
}
