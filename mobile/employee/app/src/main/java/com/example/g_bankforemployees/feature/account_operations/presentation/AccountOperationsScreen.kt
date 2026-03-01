package com.example.g_bankforemployees.feature.account_operations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.g_bankforemployees.R
import com.example.g_bankforemployees.common.presentation.component.BankTopBar
import com.example.g_bankforemployees.common.presentation.component.InfoCard
import com.example.g_bankforemployees.common.presentation.component.InlineErrorText
import com.example.g_bankforemployees.common.presentation.component.InlineLoadingText
import com.example.g_bankforemployees.common.presentation.component.ListItemCard
import com.example.g_bankforemployees.common.presentation.component.TariffCard
import com.example.g_bankforemployees.common.presentation.util.formatDateTime
import com.example.g_bankforemployees.common.domain.model.BankAccount
import com.example.g_bankforemployees.common.domain.model.CreditAccount
import com.example.g_bankforemployees.feature.account_operations.domain.model.ActionType
import com.example.g_bankforemployees.feature.account_operations.domain.model.Operation
import com.example.g_bankforemployees.feature.account_operations.domain.model.OperationStatus

private const val TRANSFER_TYPE_CREDIT = "CREDIT_ACCOUNT"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountOperationsScreen(viewModel: AccountOperationsViewModel) {
    val bankAccount by viewModel.bankAccount.collectAsStateWithLifecycle()
    val creditAccount by viewModel.creditAccount.collectAsStateWithLifecycle()
    val operations by viewModel.operations.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    AccountOperationsScreenContent(
        accountNumber = viewModel.accountNumber,
        userName = viewModel.userName,
        transferType = viewModel.transferType,
        bankAccount = bankAccount,
        creditAccount = creditAccount,
        operations = operations,
        isLoading = isLoading,
        errorMessage = errorMessage,
        onBackClick = viewModel::onBackClick,
    )
}

@Composable
private fun AccountOperationsScreenContent(
    accountNumber: String,
    userName: String,
    transferType: String,
    bankAccount: BankAccount?,
    creditAccount: CreditAccount?,
    operations: List<Operation>,
    isLoading: Boolean,
    errorMessage: String?,
    onBackClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            BankTopBar(
                title = stringResource(R.string.account_details),
                onBackClick = onBackClick,
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                InfoCard {
                    Text(
                        text = if (transferType == TRANSFER_TYPE_CREDIT) {
                            stringResource(R.string.account_type_credit)
                        } else {
                            stringResource(R.string.account_type_bank)
                        },
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = accountNumber,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    if (userName.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = userName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    if (bankAccount?.banned == true || creditAccount?.banned == true) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(R.string.banned),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                }
            }
            if (transferType != TRANSFER_TYPE_CREDIT && bankAccount != null) {
                item {
                    InfoCard {
                        Text(
                            text = stringResource(R.string.balance_format, bankAccount.balance),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            }
            if (transferType == TRANSFER_TYPE_CREDIT && creditAccount != null) {
                item {
                    TariffCard(
                        name = creditAccount.creditRateName,
                        percent = "${creditAccount.creditRatePercent}%",
                        writeOffPeriod = creditAccount.writeOffPeriod,
                    )
                }
                item {
                    InfoCard {
                        Text(
                            text = stringResource(R.string.debt_format, creditAccount.dept),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(8.dp)) }
            if (isLoading) item { InlineLoadingText() }
            errorMessage?.let { message -> item { InlineErrorText(message = message) } }
            item {
                Text(
                    text = stringResource(R.string.operations_history),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            items(operations) { operation -> OperationCard(operation = operation) }
        }
    }
}

@Composable
private fun actionTypeToTitle(actionType: ActionType?, fallback: String): String = when (actionType) {
    ActionType.OPEN_ACCOUNT -> stringResource(R.string.op_open_account)
    ActionType.CLOSE_ACCOUNT -> stringResource(R.string.op_close_account)
    ActionType.TRANSFER_RECEIVED -> stringResource(R.string.op_transfer_received)
    ActionType.TRANSFER_SENT -> stringResource(R.string.op_transfer_sent)
    ActionType.ACCOUNT_BANNED -> stringResource(R.string.account_banned)
    ActionType.ACCOUNT_UNBANNED -> stringResource(R.string.account_unbanned)
    null -> fallback
}

@Composable
private fun operationStatusToTitle(status: OperationStatus?, fallback: String): String = when (status) {
    OperationStatus.CREATED -> stringResource(R.string.status_created)
    OperationStatus.IN_PROCESS -> stringResource(R.string.status_in_process)
    OperationStatus.SUCCESS -> stringResource(R.string.status_success)
    OperationStatus.REJECTED -> stringResource(R.string.status_rejected)
    null -> fallback
}

@Composable
private fun OperationCard(operation: Operation) {
    val actionType = ActionType.fromApiValue(operation.actionType)
    val actionTypeLabel = actionTypeToTitle(actionType, operation.actionType)
    val status = OperationStatus.fromApiValue(operation.status)
    val statusLabel = operationStatusToTitle(status, operation.status)

    ListItemCard {
        Text(
            text = actionTypeLabel,
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "%.2f".format(operation.amount),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        when (actionType) {
            ActionType.TRANSFER_RECEIVED -> operation.accountNumberFrom?.let { from ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.from_account, from),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            ActionType.TRANSFER_SENT -> {
                val parts = buildList {
                    operation.recipientName?.let { add(it) }
                    operation.recipientAccountNumber?.let { add(stringResource(R.string.account_label, it)) }
                }
                if (parts.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.to_recipient, parts.joinToString(", ")),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            else -> {
                val parts = buildList {
                    operation.accountNumberFrom?.let { add("со счёта $it") }
                    operation.recipientAccountNumber?.let { add("на счёт $it") }
                }
                if (parts.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = parts.joinToString(", "),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.status_label, statusLabel),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = formatDateTime(operation.createTime),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
