package ru.patterns.shared.model.external;

import java.util.UUID;

public record AuthUser(
        UUID userId,
        Role role
) {
}
