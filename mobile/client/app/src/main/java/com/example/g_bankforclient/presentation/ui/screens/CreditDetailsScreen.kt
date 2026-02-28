package com.example.g_bankforclient.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.g_bankforclient.common.models.Account
import com.example.g_bankforclient.common.models.Credit
import com.example.g_bankforclient.presentation.state.CreditDetailsScreenState
import com.example.g_bankforclient.presentation.viewmodel.CreditDetailsViewModel
import com.example.g_bankforclient.ui.theme.BankColors
import com.example.g_bankforclient.ui.theme.GbankForClientTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreditDetailsScreen(
    creditId: String,
    onBack: () -> Unit
) {
    val viewModel: CreditDetailsViewModel = hiltViewModel()
    val screenState by viewModel.state.collectAsStateWithLifecycle()
    var amount by remember { mutableStateOf("") }
    var selectedAccountId by remember { mutableStateOf("") }

    LaunchedEffect(creditId) {
        viewModel.loadCreditDetails(creditId)
    }

    when (val state = screenState) {
        is CreditDetailsScreenState.Default -> {
            // Initialize selected account ID if not set
            LaunchedEffect(state.credit) {
                if (selectedAccountId.isEmpty()) {
                    selectedAccountId = viewModel.accounts.firstOrNull()?.id ?: ""
                }
            }
            
            DefaultCreditDetailsScreen(
                credit = state.credit,
                accounts = viewModel.accounts,
                amount = amount,
                selectedAccountId = selectedAccountId,
                onAmountChange = { amount = it },
                onSelectedAccountChange = { selectedAccountId = it },
                onBack = onBack,
                onPayCredit = { accountId, paymentAmount -> 
                    viewModel.payCredit(state.credit.id, accountId, paymentAmount)
                    amount = ""
                }
            )
        }
        
        CreditDetailsScreenState.Loading -> LoadingCreditDetailsScreen()
        
        is CreditDetailsScreenState.Error -> ErrorCreditDetailsScreen(state.message)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DefaultCreditDetailsScreen(
    credit: Credit,
    accounts: List<Account>,
    amount: String,
    selectedAccountId: String,
    onAmountChange: (String) -> Unit,
    onSelectedAccountChange: (String) -> Unit,
    onBack: () -> Unit,
    onPayCredit: (String, Double) -> Unit
) {
    val selectedAccount = accounts.find { it.id == selectedAccountId }
    val progress = ((credit.amount - credit.debt) / credit.amount * 100).toFloat()

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
                        text = credit.name,
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
                    )
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Debt Card
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(BankColors.ErrorRed, Color(0xFFF44336))
                            )
                        )
                        .padding(24.dp)
                ) {
                    Column {
                        Text(
                            text = "Остаток задолженности",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${credit.debt.toInt().toString().replace(Regex("(\\d)(?=(\\d{3})+$)"), "$1 ")} ₽",
                            style = MaterialTheme.typography.displaySmall,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "из ${credit.amount.toInt().toString().replace(Regex("(\\d)(?=(\\d{3})+$)"), "$1 ")} ₽",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                            Text(
                                text = "${credit.interestRate}% годовых",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }
                }
            }

            // Progress Card
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 2.dp,
                    shadowElevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Прогресс погашения",
                                style = MaterialTheme.typography.bodyMedium,
                                color = BankColors.SecondaryText
                            )
                            Text(
                                text = "${progress.toInt()}%",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(BankColors.LightGray)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(progress / 100f)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(MaterialTheme.colorScheme.primary)
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Погашено",
                                style = MaterialTheme.typography.bodySmall,
                                color = BankColors.SecondaryText
                            )
                            Text(
                                text = "${(credit.amount - credit.debt).toInt().toString().replace(Regex("(\\d)(?=(\\d{3})+$)"), "$1 ")} ₽",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }

            // Payment Card
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 2.dp,
                    shadowElevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Payments,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text("Погасить кредит")
                        }

                        // Account Selector
                        Column {
                            Text(
                                text = "Списать со счета",
                                style = MaterialTheme.typography.bodySmall,
                                color = BankColors.SecondaryText
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            // Simple dropdown-like list
                            accounts.forEach { account ->
                                Surface(
                                    onClick = { onSelectedAccountChange(account.id) },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (selectedAccountId == account.id)
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                    else
                                        BankColors.LightGray
                                ) {
                                    Text(
                                        text = "${account.name} - ${account.balance.toInt().toString().replace(Regex("(\\d)(?=(\\d{3})+$)"), "$1 ")} ₽",
                                        modifier = Modifier.padding(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }

                        OutlinedTextField(
                            value = amount,
                            onValueChange = onAmountChange,
                            label = { Text("Сумма платежа") },
                            placeholder = { Text("0") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(5000.0, 10000.0, 25000.0).forEach { quickAmount ->
                                OutlinedButton(
                                    onClick = { onAmountChange(quickAmount.toInt().toString()) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(quickAmount.toInt().toString().replace(Regex("(\\d)(?=(\\d{3})+$)"), "$1 "))
                                }
                            }
                        }

                        Button(
                            onClick = {
                                amount.toDoubleOrNull()?.let { value ->
                                    if (value > 0 && selectedAccount != null) {
                                        if (value > selectedAccount.balance) {
                                            // Show error
                                        } else if (value > credit.debt) {
                                            // Show error
                                        } else {
                                            onPayCredit(selectedAccountId, value)
                                        }
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.TrendingUp,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Погасить")
                        }
                    }
                }
            }

            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = BankColors.LightGray.copy(alpha = 0.5f)
                ) {
                    Text(
                        text = "Минимальный ежемесячный платеж: ${(credit.debt * 0.05).toInt().toString().replace(Regex("(\\d)(?=(\\d{3})+$)"), "$1 ")} ₽",
                        style = MaterialTheme.typography.bodySmall,
                        color = BankColors.SecondaryText,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingCreditDetailsScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorCreditDetailsScreen(message: String) {
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
fun CreditDetailsScreenPreview() {
    GbankForClientTheme {
        DefaultCreditDetailsScreen(
            credit = Credit(
                id = "1",
                name = "Ипотека",
                amount = 3_000_000.0,
                debt = 2_400_000.0,
                interestRate = 8.5
            ),
            accounts = listOf(
                Account(id = "1", name = "Основной счёт", balance = 150_000.0),
                Account(id = "2", name = "Накопительный", balance = 45_000.0)
            ),
            amount = "",
            selectedAccountId = "1",
            onAmountChange = { /* Заглушка */ },
            onSelectedAccountChange = { /* Заглушка */ },
            onBack = { /* Заглушка */ },
            onPayCredit = { _, _ -> /* Заглушка */ }
        )
    }
}
