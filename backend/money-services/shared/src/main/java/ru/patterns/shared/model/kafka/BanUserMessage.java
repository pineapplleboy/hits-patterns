package ru.patterns.shared.model.kafka;

import lombok.Data;

import java.util.UUID;

@Data
public class BanUserMessage {

    private UUID id;

    private boolean ban;
}
