package ru.patterns.shared.model.kafka;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.UUID;

@Data
public class BanUserMessage {

    @JsonProperty("Id")
    private UUID id;

    @JsonProperty("Ban")
    private boolean ban;
}
