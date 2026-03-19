package ru.patterns.account.application.service.websocket;

import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import ru.patterns.account.application.common.enums.WebSocketMessageType;
import ru.patterns.account.application.common.model.websocket.WebSocketMessage;
import ru.patterns.account.domain.entity.Operation;
import ru.patterns.account.domain.mapper.OperationMapper;

@Service
@RequiredArgsConstructor
@ExtensionMethod(OperationMapper.class)
public class OperationWebSocketPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    private final static String USER_INFO_TOPIC_NAME = "/topic/users/";

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
}
