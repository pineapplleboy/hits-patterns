package ru.patterns.gateway.application.common;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ResponseData {

    private int responseCode;

    private String responseBody;

    private IdempotencyStatus status;
}
