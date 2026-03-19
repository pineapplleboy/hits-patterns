package ru.patterns.account.application.common.model.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductBalanceUpdateModel {

    private String balance;
}
