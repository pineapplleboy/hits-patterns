package ru.patterns.account.application.utility;

import lombok.experimental.UtilityClass;
import ru.patterns.account.application.common.enums.AccountActionType;
import ru.patterns.account.domain.entity.Operation;
import ru.patterns.shared.model.enums.OperationStatus;
import ru.patterns.shared.model.enums.TransferAccountType;
import ru.patterns.shared.model.notification.NotificationModel;

import java.math.BigDecimal;
import java.util.Optional;

@UtilityClass
public class NotificationUtility {

    public NotificationModel createSenderTransferNotification(Operation operation) {
        return new NotificationModel()
                .setUserId(operation.getUserIdFrom())
                .setMessage(formSenderTransferNotificationMessage(operation));
    }

    public Optional<NotificationModel> createRecipientTransferNotification(Operation operation) {
        if (operation.getRecipientId() == null) {
            return Optional.empty();
        }

        NotificationModel notificationModel = new NotificationModel()
                .setUserId(operation.getRecipientId())
                .setMessage(formRecipientTransferNotificationMessage(operation));

        return Optional.of(notificationModel);
    }

    public String formSenderTransferNotificationMessage(Operation operation) {
        return formNotificationMessage(operation, false);
    }

    public String formRecipientTransferNotificationMessage(Operation operation) {
        return formNotificationMessage(operation, true);
    }

    private String formNotificationMessage(Operation operation, boolean recipientNotification) {
        AccountActionType actionType = operation.getActionType();
        String direction = resolveDirection(recipientNotification);

        return switch (actionType) {
            case TRANSFER, TRANSFER_SENT, TRANSFER_RECEIVED -> recipientNotification
                    ? formRecipientTransferMessage(operation)
                    : formSenderTransferMessage(operation);
            case OPEN_ACCOUNT -> resolveStatusMessage(
                    operation,
                    direction + ": счёт успешно открыт",
                    direction + ": не удалось открыть счёт. Проверьте доступность выбранной валюты."
            );
            case CLOSE_ACCOUNT -> resolveStatusMessage(
                    operation,
                    direction + ": счёт успешно закрыт",
                    direction + ": не удалось закрыть счёт. Возможно, счёт не найден или уже закрыт."
            );
            case ACCOUNT_BANNED -> resolveStatusMessage(
                    operation,
                    direction + ": счёт заблокирован",
                    direction + ": не удалось заблокировать счёт."
            );
            case ACCOUNT_UNBANNED -> resolveStatusMessage(
                    operation,
                    direction + ": счёт разблокирован",
                    direction + ": не удалось разблокировать счёт."
            );
            case CREDIT_DEPT_PERCENT -> resolveStatusMessage(
                    operation,
                    direction + ": начисление процентов по кредиту выполнено успешно",
                    direction + ": не удалось начислить проценты по кредиту."
            );
        };
    }

    private String formSenderTransferMessage(Operation operation) {
        String amount = formatAmount(operation.getAmountFrom());
        String direction = resolveDirection(false);

        if (operation.getTransferAccountType() == TransferAccountType.CREDIT_ACCOUNT) {
            return operation.getStatus() == OperationStatus.SUCCESS
                    ? direction + ": погашение кредита на сумму " + amount + " выполнено успешно"
                    : direction + ": погашение кредита на сумму " + amount + " не выполнено. Возможные причины: недостаточно средств, счёт заблокирован, кредит не найден или неактивен, либо операция доступна только для RUB.";
        }

        return operation.getStatus() == OperationStatus.SUCCESS
                ? direction + ": перевод на сумму " + amount + " выполнен успешно"
                : direction + ": перевод на сумму " + amount + " не выполнен. Возможные причины: недостаточно средств, счёт заблокирован, счёт получателя недоступен или валюта перевода не поддерживается.";
    }

    private String formRecipientTransferMessage(Operation operation) {
        String amount = formatAmount(operation.getAmountTo() != null ? operation.getAmountTo() : operation.getAmountFrom());
        String direction = resolveDirection(true);

        return operation.getStatus() == OperationStatus.SUCCESS
                ? direction + ": перевод на сумму " + amount + " зачислен успешно"
                : direction + ": перевод на сумму " + amount + " не был зачислен. Операция отклонена или отменена до завершения.";
    }

    private String resolveDirection(boolean recipientNotification) {
        return recipientNotification ? "Исходящая операция" : "Входящая операция";
    }

    private String resolveStatusMessage(Operation operation, String successMessage, String rejectedMessage) {
        return operation.getStatus() == OperationStatus.SUCCESS ? successMessage : rejectedMessage;
    }

    private String formatAmount(BigDecimal amount) {
        return amount == null ? "0" : amount.stripTrailingZeros().toPlainString();
    }
}
