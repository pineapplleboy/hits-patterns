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
import com.example.g_bankforemployees.common.presentation.component.ErrorState
import com.example.g_bankforemployees.common.presentation.component.InfoCard
import com.example.g_bankforemployees.common.presentation.component.ListItemCard
import com.example.g_bankforemployees.common.presentation.component.LoadingState
import com.example.g_bankforemployees.common.presentation.component.TariffCard
import com.example.g_bankforemployees.common.presentation.util.formatDateTime
import com.example.g_bankforemployees.feature.account_operations.domain.model.ActionType
import com.example.g_bankforemployees.feature.account_operations.domain.model.Operation
import com.example.g_bankforemployees.feature.account_operations.domain.model.OperationStatus

private const val TRANSFER_TYPE_CREDIT = "CREDIT_ACCOUNT"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountOperationsScreen(viewModel: AccountOperationsViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    AccountOperationsScreenContent(
        state = state,
        onBackClick = viewModel::onBackClick,
        onRetry = viewModel::load,
    )
}

@Composable
private fun AccountOperationsScreenContent(
    state: AccountOperationsScreenState,
    onBackClick: () -> Unit,
    onRetry: () -> Unit,
) {
    Scaffold(
        topBar = {
            BankTopBar(
                title = stringResource(R.string.account_details),
                onBackClick = onBackClick,
            )
        },
    ) { paddingValues ->
        when (state) {
            AccountOperationsScreenState.Loading -> LoadingState()
            is AccountOperationsScreenState.Error -> ErrorState(
                title = stringResource(R.string.error),
                description = state.message,
                onRetry = onRetry,
            )

            is AccountOperationsScreenState.Default -> LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                item {
                    InfoCard {
                        state.warningMessage?.let { warning ->
                            Text(
                                text = warning,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error,
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        Text(
                            text = if (state.transferType == TRANSFER_TYPE_CREDIT) {
                                stringResource(R.string.account_type_credit)
                            } else {
                                stringResource(R.string.account_type_bank)
                            },
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = state.accountNumber,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )

                        if (state.userName.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = state.userName,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }

                        if (state.bankAccount?.banned == true || state.creditAccount?.banned == true) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = stringResource(R.string.banned),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.error,
                            )
                        }
                    }
                }

                if (state.transferType != TRANSFER_TYPE_CREDIT && state.bankAccount != null) {
                    item {
                        InfoCard {
                            Text(
                                text = state.bankAccount.balanceText ?: stringResource(R.string.balance_format, state.bankAccount.balance),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                            )

                            state.bankAccount.currency?.let { currency ->
                                Spacer(modifier = Modifier.height(8.dp))
                                AccountDetailLine(
                                    label = stringResource(R.string.currency_label),
                                    value = listOf(currency.name, currency.charCode, currency.symbol)
                                        .filter { it.isNotBlank() }
                                        .joinToString(" "),
                                )
                            }

                            state.bankAccount.createTime?.takeIf { it.isNotBlank() }?.let { createTime ->
                                Spacer(modifier = Modifier.height(4.dp))
                                AccountDetailLine(
                                    label = stringResource(R.string.created_at_label),
                                    value = formatDateTime(createTime),
                                )
                            }
                        }
                    }
                }

                if (state.transferType == TRANSFER_TYPE_CREDIT && state.creditAccount != null) {
                    item {
                        TariffCard(
                            name = state.creditAccount.creditRateName,
                            percent = "${state.creditAccount.creditRatePercent}%",
                            writeOffPeriod = state.creditAccount.writeOffPeriod,
                        )
                    }
                    item {
                        InfoCard {
                            Text(
                                text = state.creditAccount.deptText ?: stringResource(R.string.debt_format, state.creditAccount.dept),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            AccountDetailLine(
                                label = stringResource(R.string.next_write_off_label),
                                value = formatDateTime(state.creditAccount.nextWriteOffDate),
                            )
                            state.isCreditExpired?.let { isExpired ->
                                Spacer(modifier = Modifier.height(4.dp))
                                AccountDetailLine(
                                    label = stringResource(R.string.credit_expired_label),
                                    value = stringResource(
                                        if (isExpired) R.string.credit_expired_yes else R.string.credit_expired_no,
                                    ),
                                )
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(8.dp)) }
                item {
                    Text(
                        text = stringResource(R.string.operations_history),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                items(state.operations) { operation ->
                    OperationCard(operation = operation)
                }
            }
        }
    }
}

@Composable
private fun AccountDetailLine(
    label: String,
    value: String,
) {
    Text(
        text = label,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    Spacer(modifier = Modifier.height(2.dp))
    Text(
        text = value,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface,
    )
}

@Composable
private fun actionTypeToTitle(actionType: ActionType?, fallback: String): String = when (actionType) {
    ActionType.OPEN_ACCOUNT -> stringResource(R.string.op_open_account)
    ActionType.CLOSE_ACCOUNT -> stringResource(R.string.op_close_account)
    ActionType.TRANSFER -> stringResource(R.string.op_transfer_sent)
    ActionType.TRANSFER_RECEIVED -> stringResource(R.string.op_transfer_received)
    ActionType.TRANSFER_SENT -> stringResource(R.string.op_transfer_sent)
    ActionType.ACCOUNT_BANNED -> stringResource(R.string.account_banned)
    ActionType.ACCOUNT_UNBANNED -> stringResource(R.string.account_unbanned)
    ActionType.CREDIT_DEPT_PERCENT -> stringResource(R.string.op_credit_dept_percent)
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
            text = operation.amount,
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

            ActionType.TRANSFER,
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
                    operation.accountNumberFrom?.let { add(stringResource(R.string.from_account, it)) }
                    operation.recipientAccountNumber?.let { add(stringResource(R.string.account_label, it)) }
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



