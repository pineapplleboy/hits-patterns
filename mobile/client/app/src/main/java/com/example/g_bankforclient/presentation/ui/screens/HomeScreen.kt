package com.example.g_bankforclient.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.g_bankforclient.common.models.Account
import com.example.g_bankforclient.presentation.state.HomeScreenState
import com.example.g_bankforclient.presentation.ui.components.AccountCard
import com.example.g_bankforclient.presentation.viewmodel.HomeViewModel
import com.example.g_bankforclient.ui.theme.BankColors
import com.example.g_bankforclient.ui.theme.GbankForClientTheme

@Composable
fun HomeScreen(
    onAccountClick: (String) -> Unit,
    onCreateAccount: () -> Unit
) {
    val viewModel: HomeViewModel = hiltViewModel()
    val screenState by viewModel.state.collectAsStateWithLifecycle()

    when (val state = screenState) {
        is HomeScreenState.Default -> DefaultHomeScreen(
            accounts = state.accounts,
            onAccountClick = onAccountClick,
            onCreateAccount = onCreateAccount
        )
        
        HomeScreenState.Loading -> LoadingHomeScreen()
        
        is HomeScreenState.Error -> ErrorHomeScreen(state.message)
    }
}

@Composable
private fun DefaultHomeScreen(
    accounts: List<Account>,
    onAccountClick: (String) -> Unit,
    onCreateAccount: () -> Unit
) {
    val totalBalance = accounts.sumOf { it.balance }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Header
        item {
            Column {
                Text(
                    text = "Добро пожаловать",
                    style = MaterialTheme.typography.bodyMedium,
                    color = BankColors.SecondaryText
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Мои финансы",
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }

        // Total Balance Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(BankColors.PurplePrimary, BankColors.PurplePrimaryLight)
                        )
                    )
                    .padding(24.dp)
            ) {
                Column {
                    Text(
                        text = "Общий баланс",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${totalBalance.toInt().toString().replace(Regex("(\\d)(?=(\\d{3})+$)"), "$1 ")} ₽",
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.AccountBalanceWallet,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${accounts.size} ${if (accounts.size == 1) "счет" else "счета"}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }
        }

        // Accounts Section Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Мои счета",
                    style = MaterialTheme.typography.headlineSmall
                )
                FilledIconButton(
                    onClick = onCreateAccount,
                    modifier = Modifier.size(40.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Создать счет",
                        tint = Color.White
                    )
                }
            }
        }

        // Accounts List
        if (accounts.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = BankColors.MediumGray.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "У вас пока нет счетов",
                        color = BankColors.MediumGray
                    )
                    Text(
                        text = "Нажмите \"+\" чтобы создать",
                        style = MaterialTheme.typography.bodySmall,
                        color = BankColors.MediumGray
                    )
                }
            }
        } else {
            items(accounts) { account ->
                AccountCard(
                    account = account,
                    onClick = { onAccountClick(account.id) }
                )
            }
        }
    }
}

@Composable
private fun LoadingHomeScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorHomeScreen(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Ошибка загрузки",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val accounts = listOf(
        Account(
            id = "1",
            name = "Основной счёт",
            balance = 123456.78,
        ),
        Account(
            id = "2",
            name = "Накопительный счёт",
            balance = 45000.00,
        )
    )

    GbankForClientTheme {
        DefaultHomeScreen(
            accounts = accounts,
            onAccountClick = { /* Пустая заглушка */ },
            onCreateAccount = { /* Пустая заглушка */ }
        )
    }
}
