package ru.patterns.shared.model.notification;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.UUID;

@Data
@Accessors(chain=true)
public class NotificationModel {

    private UUID userId;

    private String message;
}
