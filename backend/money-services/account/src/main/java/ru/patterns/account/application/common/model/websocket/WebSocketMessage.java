package ru.patterns.account.application.common.model.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.patterns.account.application.common.enums.WebSocketMessageType;

@Data
@AllArgsConstructor
public class WebSocketMessage {

    private WebSocketMessageType messageType;

    private Object body;
}
