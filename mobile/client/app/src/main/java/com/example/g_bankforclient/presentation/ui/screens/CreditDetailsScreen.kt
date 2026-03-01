package com.example.g_bankforclient.presentation.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.g_bankforclient.domain.models.Account
import com.example.g_bankforclient.domain.models.Credit
import com.example.g_bankforclient.presentation.state.CreditDetailsScreenState
import com.example.g_bankforclient.presentation.ui.components.TransactionItem
import com.example.g_bankforclient.presentation.ui.utils.formatMoney
import com.example.g_bankforclient.presentation.viewmodel.CreditDetailsViewModel
import com.example.g_bankforclient.ui.theme.BankColors
import com.example.g_bankforclient.ui.theme.GbankForClientTheme
import java.text.SimpleDateFormat
import java.util.Locale

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

    val snackbarHostState = remember { SnackbarHostState() }
    
    LaunchedEffect(creditId) {
        viewModel.loadCreditDetails(creditId)
    }

    LaunchedEffect(screenState) {
        if (screenState is CreditDetailsScreenState.Default && (screenState as CreditDetailsScreenState.Default).errorMessage != null) {
            snackbarHostState.showSnackbar(
                (screenState as CreditDetailsScreenState.Default).errorMessage ?: "Ошибка"
            )
            viewModel.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        when (val state = screenState) {
        is CreditDetailsScreenState.Default -> {
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
                isLoading = state.isLoading,
                onAmountChange = { amount = it },
                onSelectedAccountChange = { selectedAccountId = it },
                onBack = onBack,
                onRefresh = { viewModel.loadCreditDetails(creditId) },
                onPayCredit = { accountId, paymentAmount -> 
                    viewModel.payCredit(state.credit.id, accountId, paymentAmount)
                    amount = ""
                },
                modifier = Modifier.padding(paddingValues)
            )
        }
        
        CreditDetailsScreenState.Loading -> LoadingCreditDetailsScreen()
        
        is CreditDetailsScreenState.Error -> ErrorCreditDetailsScreen(state.message)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DefaultCreditDetailsScreen(
    credit: Credit,
    accounts: List<Account>,
    amount: String,
    selectedAccountId: String,
    isLoading: Boolean,
    onAmountChange: (String) -> Unit,
    onSelectedAccountChange: (String) -> Unit,
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    onPayCredit: (String, Double) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedAccount = accounts.find { it.id == selectedAccountId }

    Scaffold(
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 0.dp
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Назад",
                                tint = BankColors.MediumGray
                            )
                        }
                        IconButton(
                            onClick = onRefresh,
                            modifier = Modifier.padding(end = 8.dp),
                            enabled = !isLoading
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Обновить",
                                tint = if (isLoading) BankColors.MediumGray.copy(alpha = 0.5f) else BankColors.MediumGray
                            )
                        }
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
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = paddingValues.calculateTopPadding() + 24.dp,
                bottom = 24.dp,
                start = 24.dp,
                end = 24.dp
            ),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
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
                            text = credit.debt.formatMoney(),
                            style = MaterialTheme.typography.displaySmall,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            if (credit.amount > credit.debt) {
                                Text(
                                    text = "из ${credit.amount.formatMoney()}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            } else {
                                Spacer(modifier = Modifier)
                            }
                            Text(
                                text = "${credit.interestRate}% годовых",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }
                }
            }

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

                        var isAccountsExpanded by remember { mutableStateOf(false) }
                        Column {
                            Text(
                                text = "Списать со счета",
                                style = MaterialTheme.typography.bodySmall,
                                color = BankColors.SecondaryText
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            selectedAccount?.let { account ->
                                Surface(
                                    onClick = {
                                        if (accounts.size > 1) {
                                            isAccountsExpanded = !isAccountsExpanded
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "${account.name} - ${account.balance.formatMoney()}"
                                        )
                                        if (accounts.size > 1) {
                                            Icon(
                                                imageVector = if (isAccountsExpanded)
                                                    Icons.Default.ArrowDropUp
                                                else
                                                    Icons.Default.ArrowDropDown,
                                                contentDescription = null,
                                                tint = BankColors.SecondaryText
                                            )
                                        }
                                    }
                                }
                            }

                            if (accounts.size > 1) {
                                AnimatedVisibility(
                                    visible = isAccountsExpanded,
                                    enter = expandVertically() + fadeIn(),
                                    exit = shrinkVertically() + fadeOut()
                                ) {
                                    Column {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        accounts.filter { it.id != selectedAccountId }
                                            .forEach { account ->
                                                Surface(
                                                    onClick = {
                                                        onSelectedAccountChange(account.id)
                                                        isAccountsExpanded = false
                                                    },
                                                    modifier = Modifier.fillMaxWidth(),
                                                    shape = RoundedCornerShape(12.dp),
                                                    color = BankColors.LightGray
                                                ) {
                                                    Text(
                                                        text = "${account.name} - ${account.balance.formatMoney()}",
                                                        modifier = Modifier.padding(16.dp)
                                                    )
                                                }
                                                Spacer(modifier = Modifier.height(8.dp))
                                            }
                                    }
                                }
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

                        Button(
                            onClick = {
                                amount.toDoubleOrNull()?.let { value ->
                                    if (value > 0 && selectedAccount != null) {
                                        if (value > selectedAccount.balance) {
                                        } else if (value > credit.debt) {
                                        } else {
                                            onPayCredit(selectedAccountId, value)
                                        }
                                    }
                                }
                            },
                            enabled = !isLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
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

            if (credit.nextWriteOffDate != null || credit.writeOffPeriod != null) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = BankColors.LightGray.copy(alpha = 0.5f)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            credit.nextWriteOffDate?.let { date ->
                                Text(
                                    text = "Следующий платёж: ${
                                        SimpleDateFormat(
                                            "d MMM yyyy",
                                            Locale.getDefault()
                                        ).format(date)
                                    }",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = BankColors.SecondaryText
                                )
                            }
                            credit.writeOffPeriod?.let { period ->
                                if (credit.nextWriteOffDate != null) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                }
                                Text(
                                    text = "Период списания: $period",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = BankColors.SecondaryText
                                )
                            }
                        }
                    }
                }
            }

            if (credit.transactions.isNotEmpty()) {
                item {
                    var isExpanded by remember { mutableStateOf(false) }

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
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { isExpanded = !isExpanded },
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.History,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Text(
                                        text = "История операций",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "${credit.transactions.size}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = BankColors.SecondaryText
                                    )
                                    Icon(
                                        imageVector = if (isExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                                        contentDescription = if (isExpanded) "Свернуть" else "Развернуть",
                                        tint = BankColors.SecondaryText
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            credit.transactions.take(3).forEach { transaction ->
                                TransactionItem(transaction = transaction)
                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            AnimatedVisibility(
                                visible = isExpanded,
                                enter = expandVertically() + fadeIn(),
                                exit = shrinkVertically() + fadeOut()
                            ) {
                                Column {
                                    credit.transactions.drop(3).forEach { transaction ->
                                        TransactionItem(transaction = transaction)
                                        Spacer(modifier = Modifier.height(8.dp))
                                    }
                                }
                            }

                            if (credit.transactions.size > 3 && !isExpanded) {
                                Text(
                                    text = "И ещё ${credit.transactions.size - 3} операций...",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = BankColors.SecondaryText,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
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
                interestRate = 8.5,
                writeOffPeriod = "Ежемесячно",
                nextWriteOffDate = java.util.Date()
            ),
            accounts = listOf(
                Account(id = "1", name = "Основной счёт", balance = 150_000.0),
                Account(id = "2", name = "Накопительный", balance = 45_000.0)
            ),
            amount = "",
            selectedAccountId = "1",
            isLoading = false,
            onAmountChange = { },
            onSelectedAccountChange = { },
            onBack = { },
            onPayCredit = { _, _ -> },
            onRefresh = {}
        )
    }
}
