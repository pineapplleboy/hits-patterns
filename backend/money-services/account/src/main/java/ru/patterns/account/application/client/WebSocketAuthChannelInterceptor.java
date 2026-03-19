package ru.patterns.account.application.client;

import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import ru.patterns.account.application.service.websocket.OperationWebSocketPublisher;
import ru.patterns.shared.utility.AuthUtility;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class WebSocketAuthChannelInterceptor implements ChannelInterceptor {

    private static final String AUTHORIZATION = "Authorization";

    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null || accessor.getCommand() == null) {
            return message;
        }

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            storeAuthorization(accessor);
        }

        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            authorizeUserTopicSubscription(accessor);
        }

        return message;
    }

    private void storeAuthorization(StompHeaderAccessor accessor) {
        String authorizationHeader = resolveAuthorizationHeader(accessor);
        if (authorizationHeader == null) {
            return;
        }

        AuthUtility.isAuthorized(authorizationHeader);

        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
        if (sessionAttributes != null) {
            sessionAttributes.put(AUTHORIZATION, authorizationHeader);
        }
    }

    private void authorizeUserTopicSubscription(StompHeaderAccessor accessor) {
        String destination = accessor.getDestination();
        if (destination == null || !destination.startsWith(OperationWebSocketPublisher.USER_INFO_TOPIC_NAME)) {
            return;
        }

        String authorizationHeader = resolveAuthorizationHeader(accessor);
        UUID userId = extractUserId(destination);
        AuthUtility.checkUserIdEqualityOrUserEmployee(authorizationHeader, userId);
    }

    private String resolveAuthorizationHeader(StompHeaderAccessor accessor) {
        List<String> authorizationHeaders = accessor.getNativeHeader(AUTHORIZATION);
        if (authorizationHeaders != null && !authorizationHeaders.isEmpty()) {
            return authorizationHeaders.getFirst();
        }

        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
        if (sessionAttributes == null) {
            return null;
        }

        Object authorizationHeader = sessionAttributes.get(AUTHORIZATION);
        return authorizationHeader instanceof String value ? value : null;
    }

    private UUID extractUserId(String destination) {
        String userId = destination.substring(OperationWebSocketPublisher.USER_INFO_TOPIC_NAME.length());
        return UUID.fromString(userId);
    }
}
