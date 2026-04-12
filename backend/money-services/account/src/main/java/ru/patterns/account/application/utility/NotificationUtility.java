package ru.patterns.account.application.utility;

import lombok.experimental.UtilityClass;
import ru.patterns.account.application.common.enums.AccountActionType;
import ru.patterns.account.domain.entity.Operation;
import ru.patterns.shared.model.enums.OperationStatus;
import ru.patterns.shared.model.enums.TransferAccountType;
import ru.patterns.shared.model.notification.NotificationModel;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@UtilityClass
public class NotificationUtility {

    public NotificationModel createSenderTransferNotification(Operation operation) {
        return new NotificationModel()
                .setUserId(operation.getUserIdFrom())
                .setMessage(formNotificationMessage(operation, operation.getUserIdFrom()));
    }

    public Optional<NotificationModel> createRecipientTransferNotification(Operation operation) {
        if (operation.getRecipientId() == null) {
            return Optional.empty();
        }

        NotificationModel notificationModel = new NotificationModel()
                .setUserId(operation.getRecipientId())
                .setMessage(formNotificationMessage(operation, operation.getRecipientId()));

        return Optional.of(notificationModel);
    }

    private String formNotificationMessage(Operation operation, UUID targetUserId) {
        AccountActionType actionType = operation.getActionType();

        return switch (actionType) {
            case TRANSFER, TRANSFER_SENT, TRANSFER_RECEIVED -> isIncomingForUser(operation, targetUserId)
                    ? formIncomingTransferMessage(operation)
                    : formOutgoingTransferMessage(operation);
            case OPEN_ACCOUNT -> resolveStatusMessage(
                    operation,
                    "Счёт успешно открыт",
                    "Не удалось открыть счёт. Проверьте доступность выбранной валюты."
            );
            case CLOSE_ACCOUNT -> resolveStatusMessage(
                    operation,
                    "Счёт успешно закрыт",
                    "Не удалось закрыть счёт. Возможно, счёт не найден или уже закрыт."
            );
            case ACCOUNT_BANNED -> resolveStatusMessage(
                    operation,
                    "Счёт заблокирован",
                    "Не удалось заблокировать счёт."
            );
            case ACCOUNT_UNBANNED -> resolveStatusMessage(
                    operation,
                    "Счёт разблокирован",
                    "Не удалось разблокировать счёт."
            );
            case CREDIT_DEPT_PERCENT -> resolveStatusMessage(
                    operation,
                    "Начисление процентов по кредиту выполнено успешно",
                    "Не удалось начислить проценты по кредиту."
            );
        };
    }

    private String formOutgoingTransferMessage(Operation operation) {
        String amount = formatAmount(operation.getAmountFrom());

        if (operation.getTransferAccountType() == TransferAccountType.CREDIT_ACCOUNT) {
            return operation.getStatus() == OperationStatus.SUCCESS
                    ? "Погашение кредита на сумму " + amount + " выполнено успешно"
                    : "Погашение кредита на сумму " + amount + " не выполнено. Возможные причины: недостаточно средств, счёт заблокирован, кредит не найден или неактивен, либо операция доступна только для RUB.";
        }

        return operation.getStatus() == OperationStatus.SUCCESS
                ? "Перевод на сумму " + amount + " выполнен успешно"
                : "Перевод на сумму " + amount + " не выполнен. Возможные причины: недостаточно средств, счёт заблокирован, счёт получателя недоступен или валюта перевода не поддерживается.";
    }

    private String formIncomingTransferMessage(Operation operation) {
        String amount = formatAmount(operation.getAmountTo() != null ? operation.getAmountTo() : operation.getAmountFrom());

        return operation.getStatus() == OperationStatus.SUCCESS
                ? "Перевод на сумму " + amount + " зачислен успешно"
                : "Перевод на сумму " + amount + " не был зачислен. Операция отклонена или отменена до завершения.";
    }

    private boolean isIncomingForUser(Operation operation, UUID targetUserId) {
        return targetUserId != null
                && targetUserId.equals(operation.getRecipientId())
                && !targetUserId.equals(operation.getUserIdFrom());
    }

    private String resolveStatusMessage(Operation operation, String successMessage, String rejectedMessage) {
        return operation.getStatus() == OperationStatus.SUCCESS ? successMessage : rejectedMessage;
    }

    private String formatAmount(BigDecimal amount) {
        return amount == null ? "0" : amount.stripTrailingZeros().toPlainString();
    }
}
