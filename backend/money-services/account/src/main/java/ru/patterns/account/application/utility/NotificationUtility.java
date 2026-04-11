package ru.patterns.account.application.utility;

import lombok.experimental.UtilityClass;
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
        String operationType = resolveOperationType(operation.getTransferAccountType());
        String statusText = resolveStatusText(operation.getStatus());
        String amount = formatAmount(operation.getAmountFrom());

        return operationType + " на сумму " + amount + " " + statusText;
    }

    public String formRecipientTransferNotificationMessage(Operation operation) {
        String amount = formatAmount(operation.getAmountTo());
        String statusText = resolveStatusText(operation.getStatus());

        return "Перевод на сумму " + amount + " " + statusText;
    }

    private String resolveOperationType(TransferAccountType transferAccountType) {
        return transferAccountType == TransferAccountType.CREDIT_ACCOUNT
                ? "Погашение кредита"
                : "Перевод";
    }

    private String resolveStatusText(OperationStatus status) {
        return status == OperationStatus.SUCCESS
                ? "выполнен успешно"
                : "не выполнен";
    }

    private String formatAmount(BigDecimal amount) {
        return amount == null ? "0" : amount.stripTrailingZeros().toPlainString();
    }
}
