package com.example.g_bankforclient.presentation.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.g_bankforclient.domain.models.Transaction
import com.example.g_bankforclient.domain.models.TransactionType
import com.example.g_bankforclient.presentation.state.TransactionHistoryScreenState
import com.example.g_bankforclient.presentation.ui.components.TransactionItem
import com.example.g_bankforclient.presentation.viewmodel.TransactionHistoryViewModel
import com.example.g_bankforclient.ui.theme.BankColors
import com.example.g_bankforclient.ui.theme.GbankForClientTheme
import java.util.Date


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionHistoryScreen(
    accountId: String,
    onBack: () -> Unit
) {
    val viewModel: TransactionHistoryViewModel = hiltViewModel()
    val screenState by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(accountId) {
        viewModel.loadTransactionHistory(accountId)
    }

    when (val state = screenState) {
        is TransactionHistoryScreenState.Default -> DefaultTransactionHistoryScreen(
            transactions = state.transactions,
            onBack = onBack
        )
        
        TransactionHistoryScreenState.Loading -> LoadingTransactionHistoryScreen()
        
        is TransactionHistoryScreenState.Error -> ErrorTransactionHistoryScreen(state.message)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DefaultTransactionHistoryScreen(
    transactions: List<Transaction>,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 0.dp
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Назад",
                            tint = BankColors.MediumGray
                        )
                    }
                    Text(
                        text = "История операций",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
                    )
                }
            }
        }
    ) { paddingValues ->
        if (transactions.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = BankColors.MediumGray.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Нет операций",
                    color = BankColors.MediumGray
                )
                Text(
                    text = "История операций пуста",
                    style = MaterialTheme.typography.bodySmall,
                    color = BankColors.MediumGray
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(transactions) { transaction ->
                    TransactionItem(transaction = transaction)
                }
            }
        }
    }
}

@Composable
private fun LoadingTransactionHistoryScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorTransactionHistoryScreen(message: String) {
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
fun TransactionHistoryScreenPreview() {
    GbankForClientTheme {
        DefaultTransactionHistoryScreen(
            transactions = listOf(
                Transaction(
                    id = "1",
                    accountId = "acc_1",
                    type = TransactionType.DEPOSIT,
                    amount = 15000.0,
                    date = Date(System.currentTimeMillis()),
                    description = "Пополнение с карты"
                ),
                Transaction(
                    id = "2",
                    accountId = "acc_1",
                    type = TransactionType.WITHDRAWAL,
                    amount = 5000.0,
                    date = Date(System.currentTimeMillis() - 3600000),
                    description = "Снятие наличных"
                ),
                Transaction(
                    id = "3",
                    accountId = "acc_1",
                    type = TransactionType.CREDIT_PAYMENT,
                    amount = 2500.0,
                    date = Date(System.currentTimeMillis() - 7200000),
                    description = "Оплата кредита"
                )
            ),
            onBack = {}
        )
    }
}
