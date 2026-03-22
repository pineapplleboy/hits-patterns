package com.example.g_bankforemployees.feature.client_details.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.g_bankforemployees.R
import com.example.g_bankforemployees.common.domain.model.BankAccount
import com.example.g_bankforemployees.common.domain.model.CreditAccount
import com.example.g_bankforemployees.common.presentation.component.BankTopBar
import com.example.g_bankforemployees.common.presentation.component.CommonTabRow
import com.example.g_bankforemployees.common.presentation.component.ErrorState
import com.example.g_bankforemployees.common.presentation.component.InfoCard
import com.example.g_bankforemployees.common.presentation.component.ListItemCard
import com.example.g_bankforemployees.common.presentation.component.LoadingState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientDetailsScreen(viewModel: ClientDetailsViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ClientDetailsScreenContent(
        state = state,
        onSelectedTabIndexChange = viewModel::onSelectedTabIndexChange,
        onBackClick = viewModel::onBackClick,
        onAccountClick = viewModel::onAccountClick,
        onCreditAccountClick = viewModel::onCreditAccountClick,
        onCreditHistoryClick = viewModel::onCreditHistoryClick,
        onRetry = viewModel::loadAccounts,
    )
}

@Composable
private fun ClientDetailsScreenContent(
    state: ClientDetailsScreenState,
    onSelectedTabIndexChange: (Int) -> Unit,
    onBackClick: () -> Unit,
    onAccountClick: (String) -> Unit,
    onCreditAccountClick: (String) -> Unit,
    onCreditHistoryClick: () -> Unit,
    onRetry: () -> Unit,
) {
    Scaffold(
        topBar = {
            val title = (state as? ClientDetailsScreenState.Default)?.userName
            BankTopBar(
                title = title?.ifEmpty { stringResource(R.string.user_default) } ?: stringResource(R.string.user_default),
                onBackClick = onBackClick,
            )
        },
    ) { paddingValues ->
        when (state) {
            ClientDetailsScreenState.Loading -> LoadingState()
            is ClientDetailsScreenState.Error -> ErrorState(
                title = stringResource(R.string.error),
                description = state.message,
                onRetry = onRetry,
            )
            is ClientDetailsScreenState.Default -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            ) {
                InfoCard {
                    Text(
                        text = state.userName.ifEmpty { stringResource(R.string.user_default) },
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                    if (state.userPhone.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = state.userPhone,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = onCreditHistoryClick) {
                        Text(text = stringResource(R.string.credit_history_button))
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                val tabs = listOf(stringResource(R.string.tab_accounts), stringResource(R.string.tab_credits))
                CommonTabRow(
                    titles = tabs,
                    selectedTabIndex = state.selectedTabIndex,
                    onSelectedTabIndexChange = onSelectedTabIndexChange,
                )
                Spacer(modifier = Modifier.height(8.dp))
                when (state.selectedTabIndex) {
                    0 -> BankAccountsTab(accounts = state.bankAccounts, onAccountClick = onAccountClick)
                    1 -> CreditAccountsTab(credits = state.creditAccounts, onCreditAccountClick = onCreditAccountClick)
                }
            }
        }
    }
}

@Composable
private fun BankAccountsTab(
    accounts: List<BankAccount>,
    onAccountClick: (String) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(accounts) { account ->
            ListItemCard(
                modifier = Modifier.padding(vertical = 2.dp),
                onClick = { onAccountClick(account.accountNumber) },
            ) {
                Text(
                    text = account.accountNumber,
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }
    }
}

@Composable
private fun CreditAccountsTab(
    credits: List<CreditAccount>,
    onCreditAccountClick: (String) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(credits) { credit ->
            ListItemCard(
                modifier = Modifier.padding(vertical = 2.dp),
                onClick = { onCreditAccountClick(credit.accountNumber) },
            ) {
                Text(
                    text = credit.accountNumber,
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = credit.creditRateName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
