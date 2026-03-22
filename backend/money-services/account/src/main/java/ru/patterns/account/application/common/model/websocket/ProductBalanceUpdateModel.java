package ru.patterns.account.application.common.model.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class ProductBalanceUpdateModel {

    private UUID accountId;

    private String balance;
}
