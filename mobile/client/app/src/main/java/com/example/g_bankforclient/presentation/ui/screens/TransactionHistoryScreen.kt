package com.example.g_bankforclient.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.g_bankforclient.common.models.Transaction
import com.example.g_bankforclient.common.models.TransactionType
import com.example.g_bankforclient.presentation.ui.components.TransactionItem
import com.example.g_bankforclient.ui.theme.BankColors
import com.example.g_bankforclient.ui.theme.GbankForClientTheme
import java.util.Date


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionHistoryScreen(
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
//                    imageVector = Icons.Default.Receipt,
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

@Preview(showBackground = true)
@Composable
fun TransactionHistoryScreenPreview() {
    GbankForClientTheme {
        TransactionHistoryScreen(
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
            onBack = { /* Заглушка */ }
        )
    }
}
