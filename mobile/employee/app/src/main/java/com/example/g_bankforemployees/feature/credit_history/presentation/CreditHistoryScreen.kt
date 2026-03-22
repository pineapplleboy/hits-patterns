package com.example.g_bankforemployees.feature.credit_history.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import com.example.g_bankforemployees.common.presentation.util.formatDateTime
import com.example.g_bankforemployees.feature.account_operations.domain.model.ActionType
import com.example.g_bankforemployees.feature.account_operations.domain.model.Operation
import com.example.g_bankforemployees.feature.account_operations.domain.model.OperationStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreditHistoryScreen(viewModel: CreditHistoryViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    CreditHistoryScreenContent(
        state = state,
        onBackClick = viewModel::onBackClick,
        onRetry = viewModel::load,
    )
}

@Composable
private fun CreditHistoryScreenContent(
    state: CreditHistoryScreenState,
    onBackClick: () -> Unit,
    onRetry: () -> Unit,
) {
    Scaffold(
        topBar = {
            BankTopBar(
                title = stringResource(R.string.credit_history_title),
                onBackClick = onBackClick,
            )
        },
    ) { paddingValues ->
        when (state) {
            CreditHistoryScreenState.Loading -> LoadingState()
            is CreditHistoryScreenState.Error -> ErrorState(
                title = stringResource(R.string.error),
                description = state.message,
                onRetry = onRetry,
            )
            is CreditHistoryScreenState.Default -> LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                item {
                    InfoCard {
                        if (state.userName.isNotEmpty()) {
                            Text(
                                text = state.userName,
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        Text(
                            text = stringResource(R.string.credit_rating_value, state.creditRating.rating),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.total_credits_value, state.creditRating.totalCreditCounter),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = stringResource(R.string.closed_credits_value, state.creditRating.closedCreditCounter),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = stringResource(R.string.active_credit_amount_value, state.creditRating.activeCreditAmount),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = stringResource(R.string.expired_operations_amount_value, state.creditRating.expiredOperationsAmount),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        state.warningMessage?.let { warning ->
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = warning,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error,
                            )
                        }
                    }
                }

                item {
                    Text(
                        text = stringResource(R.string.expired_operations_title),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                if (state.expiredOperations.isEmpty()) {
                    item {
                        InfoCard {
                            Text(
                                text = stringResource(R.string.no_expired_operations),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                } else {
                    items(state.expiredOperations) { operation ->
                        CreditHistoryOperationCard(operation = operation)
                    }
                }
            }
        }
    }
}

@Composable
private fun CreditHistoryOperationCard(operation: Operation) {
    val actionType = ActionType.fromApiValue(operation.actionType)
    val actionTypeLabel = when (actionType) {
        ActionType.OPEN_ACCOUNT -> stringResource(R.string.op_open_account)
        ActionType.CLOSE_ACCOUNT -> stringResource(R.string.op_close_account)
        ActionType.TRANSFER -> stringResource(R.string.op_transfer_sent)
        ActionType.TRANSFER_RECEIVED -> stringResource(R.string.op_transfer_received)
        ActionType.TRANSFER_SENT -> stringResource(R.string.op_transfer_sent)
        ActionType.ACCOUNT_BANNED -> stringResource(R.string.account_banned)
        ActionType.ACCOUNT_UNBANNED -> stringResource(R.string.account_unbanned)
        ActionType.CREDIT_DEPT_PERCENT -> stringResource(R.string.op_credit_dept_percent)
        null -> operation.actionType
    }
    val status = OperationStatus.fromApiValue(operation.status)
    val statusLabel = when (status) {
        OperationStatus.CREATED -> stringResource(R.string.status_created)
        OperationStatus.IN_PROCESS -> stringResource(R.string.status_in_process)
        OperationStatus.SUCCESS -> stringResource(R.string.status_success)
        OperationStatus.REJECTED -> stringResource(R.string.status_rejected)
        null -> operation.status
    }

    ListItemCard {
        Column {
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
            operation.accountNumberFrom?.let { fromAccount ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.from_account, fromAccount),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            operation.recipientAccountNumber?.let { recipientAccount ->
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = stringResource(R.string.account_label, recipientAccount),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
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
}
