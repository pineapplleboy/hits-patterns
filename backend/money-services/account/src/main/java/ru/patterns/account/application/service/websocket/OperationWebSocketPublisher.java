package ru.patterns.account.application.service.websocket;

import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import ru.patterns.account.application.common.enums.WebSocketMessageType;
import ru.patterns.account.application.common.model.websocket.ProductBalanceUpdateModel;
import ru.patterns.account.application.common.model.websocket.WebSocketMessage;
import ru.patterns.account.application.utility.CurrencySymbolUtility;
import ru.patterns.account.domain.entity.BankAccount;
import ru.patterns.account.domain.entity.CreditAccount;
import ru.patterns.account.domain.entity.Operation;
import ru.patterns.account.domain.mapper.OperationMapper;

@Service
@RequiredArgsConstructor
@ExtensionMethod(OperationMapper.class)
public class OperationWebSocketPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    public final static String USER_INFO_TOPIC_NAME = "/topic/users/";

    public void publishOperationUpdated(Operation operation) {
        WebSocketMessage event = new WebSocketMessage(WebSocketMessageType.OPERATION_STATUS_UPDATE, operation.toStatusModel());

        if (operation.getUserIdFrom() != null) {
            messagingTemplate.convertAndSend(USER_INFO_TOPIC_NAME + operation.getUserIdFrom(), event);
        }

        if (operation.getRecipientId() != null) {
            messagingTemplate.convertAndSend(USER_INFO_TOPIC_NAME + operation.getRecipientId(), event);
        }
    }

    public void publishOperationCreated(Operation operation) {
        WebSocketMessage eventAuthor = new WebSocketMessage(WebSocketMessageType.OPERATION_CREATE,
                operation.toModel(operation.getAccountNumberFrom()));
        WebSocketMessage eventReceiver = new WebSocketMessage(WebSocketMessageType.OPERATION_STATUS_UPDATE,
                operation.toModel(operation.getRecipientAccountNumber()));

        if (operation.getUserIdFrom() != null) {
            messagingTemplate.convertAndSend(USER_INFO_TOPIC_NAME + operation.getUserIdFrom(), eventAuthor);
        }

        if (operation.getRecipientId() != null) {
            messagingTemplate.convertAndSend(USER_INFO_TOPIC_NAME + operation.getRecipientId(), eventReceiver);
        }
    }

    public void publishAccountMoneyReceiving(BankAccount account) {
        WebSocketMessage event = new WebSocketMessage(WebSocketMessageType.BANK_ACCOUNT_SUM_UPDATE,
                new ProductBalanceUpdateModel(account.getBalance() + CurrencySymbolUtility.getCurrencySymbol(account.getCurrencyId())));

        messagingTemplate.convertAndSend(USER_INFO_TOPIC_NAME + account.getUserId(), event);
    }

    public void publishAccountMoneyReceiving(CreditAccount account) {
        WebSocketMessage event = new WebSocketMessage(WebSocketMessageType.CREDIT_ACCOUNT_DEPT_UPDATE,
                new ProductBalanceUpdateModel(account.getDept() + CurrencySymbolUtility.getCurrencySymbol(account.getCurrencyId())));

        messagingTemplate.convertAndSend(USER_INFO_TOPIC_NAME + account.getUserId(), event);
    }
}
