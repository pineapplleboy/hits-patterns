package com.example.g_bankforclient.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.g_bankforclient.domain.models.Account
import com.example.g_bankforclient.presentation.state.HomeScreenState
import com.example.g_bankforclient.presentation.ui.components.AccountCard
import com.example.g_bankforclient.presentation.ui.utils.formatMoney
import com.example.g_bankforclient.presentation.viewmodel.HomeViewModel
import com.example.g_bankforclient.ui.theme.BankColors
import com.example.g_bankforclient.ui.theme.GbankForClientTheme

@Composable
fun HomeScreen(
    onAccountClick: (String) -> Unit,
    onCreateAccount: () -> Unit,
    onLogout: () -> Unit = {}
) {
    val viewModel: HomeViewModel = hiltViewModel()
    val screenState by viewModel.state.collectAsStateWithLifecycle()

    val handleLogout: () -> Unit = {
        viewModel.logout()
        onLogout()
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadAccounts()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    when (val state = screenState) {
        is HomeScreenState.Default -> DefaultHomeScreen(
            accounts = state.accounts,
            isLoading = state.isLoading,
            onAccountClick = onAccountClick,
            onCreateAccount = onCreateAccount,
            onLogout = handleLogout
        )

        HomeScreenState.Loading -> DefaultHomeScreen(
            accounts = emptyList(),
            isLoading = true,
            onAccountClick = onAccountClick,
            onCreateAccount = onCreateAccount,
            onLogout = handleLogout
        )
        
        is HomeScreenState.Error -> ErrorHomeScreen(state.message)
    }
}

@Composable
private fun DefaultHomeScreen(
    accounts: List<Account>,
    isLoading: Boolean = false,
    onAccountClick: (String) -> Unit,
    onCreateAccount: () -> Unit,
    onLogout: () -> Unit = {}
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

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
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
                IconButton(onClick = onLogout) {
                    Icon(
                        imageVector = Icons.Filled.Logout,
                        contentDescription = "Выйти",
                        tint = BankColors.MediumGray
                    )
                }
            }
        }

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
                        text = totalBalance.formatMoney(),
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

        if (isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        strokeWidth = 3.dp
                    )
                }
            }
        }

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
                    onClick = {
                        if (!account.banned) {
                            onAccountClick(account.id)
                        }
                    }
                )
            }
        }
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
            isLoading = false,
            onAccountClick = { /* Пустая заглушка */ },
            onCreateAccount = { /* Пустая заглушка */ }
        )
    }
}
