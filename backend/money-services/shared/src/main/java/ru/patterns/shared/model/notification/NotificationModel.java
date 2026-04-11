package ru.patterns.shared.model.notification;

import lombok.Data;

import java.util.UUID;

@Data
public class NotificationModel {

    private UUID userId;

    private String message;
}
