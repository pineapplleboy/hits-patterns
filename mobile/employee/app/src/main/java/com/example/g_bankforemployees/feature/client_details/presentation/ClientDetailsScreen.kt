package com.example.g_bankforemployees.feature.client_details.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.g_bankforemployees.R
import com.example.g_bankforemployees.common.presentation.component.BankTopBar
import com.example.g_bankforemployees.common.presentation.component.InfoCard
import com.example.g_bankforemployees.common.presentation.component.InlineErrorText
import com.example.g_bankforemployees.common.presentation.component.InlineLoadingText
import com.example.g_bankforemployees.common.presentation.component.ListItemCard
import com.example.g_bankforemployees.common.presentation.util.formatDateTime
import com.example.g_bankforemployees.common.domain.model.BankAccount
import com.example.g_bankforemployees.common.domain.model.CreditAccount

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientDetailsScreen(viewModel: ClientDetailsViewModel) {
    val bankAccounts by viewModel.bankAccounts.collectAsStateWithLifecycle()
    val creditAccounts by viewModel.creditAccounts.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    val selectedTabIndex by viewModel.selectedTabIndex.collectAsStateWithLifecycle()
    ClientDetailsScreenContent(
        userName = viewModel.userName,
        userPhone = viewModel.userPhone,
        bankAccounts = bankAccounts,
        creditAccounts = creditAccounts,
        isLoading = isLoading,
        errorMessage = errorMessage,
        selectedTabIndex = selectedTabIndex,
        onSelectedTabIndexChange = viewModel::onSelectedTabIndexChange,
        onBackClick = viewModel::onBackClick,
        onAccountClick = viewModel::onAccountClick,
        onCreditAccountClick = viewModel::onCreditAccountClick,
    )
}

@Composable
private fun ClientDetailsScreenContent(
    userName: String,
    userPhone: String,
    bankAccounts: List<BankAccount>,
    creditAccounts: List<CreditAccount>,
    isLoading: Boolean,
    errorMessage: String?,
    selectedTabIndex: Int,
    onSelectedTabIndexChange: (Int) -> Unit,
    onBackClick: () -> Unit,
    onAccountClick: (String) -> Unit,
    onCreditAccountClick: (String) -> Unit,
) {
    Scaffold(
        topBar = {
            BankTopBar(
                title = userName.ifEmpty { stringResource(R.string.user_default) },
                onBackClick = onBackClick,
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 12.dp),
        ) {
            InfoCard {
                Text(
                    text = userName.ifEmpty { stringResource(R.string.user_default) },
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
                if (userPhone.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = userPhone,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            if (isLoading) InlineLoadingText()
            errorMessage?.let { InlineErrorText(message = it) }

            val tabs = listOf(stringResource(R.string.tab_accounts), stringResource(R.string.tab_credits))
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { onSelectedTabIndexChange(index) },
                        text = { Text(text = title, style = MaterialTheme.typography.titleMedium) },
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            when (selectedTabIndex) {
                0 -> BankAccountsTab(accounts = bankAccounts, onAccountClick = onAccountClick)
                1 -> CreditAccountsTab(credits = creditAccounts, onCreditAccountClick = onCreditAccountClick)
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
                if (account.banned) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.banned),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.balance_format, account.balance),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                if (credit.banned) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.banned),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.debt_format, credit.dept),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.credit_rate_format, credit.creditRateName, credit.creditRatePercent),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.write_off_period_label, credit.writeOffPeriod),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = stringResource(R.string.next_write_off, formatDateTime(credit.nextWriteOffDate)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
