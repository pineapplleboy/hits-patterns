package com.example.g_bankforclient.presentation.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.g_bankforclient.domain.models.Credit
import com.example.g_bankforclient.domain.models.CreditRate
import com.example.g_bankforclient.presentation.state.CreditsScreenState
import com.example.g_bankforclient.presentation.ui.components.CreditCard
import com.example.g_bankforclient.presentation.ui.utils.formatMoney
import com.example.g_bankforclient.presentation.viewmodel.CreditsViewModel
import com.example.g_bankforclient.ui.theme.BankColors
import com.example.g_bankforclient.ui.theme.GbankForClientTheme
import java.util.UUID

@Composable
fun CreditsScreen(
    onCreditClick: (String) -> Unit,
    onCreateCredit: () -> Unit
) {
    val viewModel: CreditsViewModel = hiltViewModel()
    val screenState by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadCreditsAndRates()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(screenState) {
        if (screenState is CreditsScreenState.Default && (screenState as CreditsScreenState.Default).errorMessage != null) {
            snackbarHostState.showSnackbar(
                (screenState as CreditsScreenState.Default).errorMessage ?: "Ошибка"
            )
            viewModel.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        when (val state = screenState) {
            is CreditsScreenState.Default -> DefaultCreditsScreen(
                credits = state.credits,
                creditRates = state.creditRates,
                isLoading = state.isLoading,
                onCreditClick = onCreditClick,
                onCreateCredit = onCreateCredit,
                onTakeCredit = { rateId, sum, bankAccountNum ->
                    viewModel.takeCredit(
                        rateId,
                        sum,
                        bankAccountNum
                    )
                },
                modifier = Modifier.padding(paddingValues)
            )

            CreditsScreenState.Loading -> DefaultCreditsScreen(
                credits = emptyList(),
                creditRates = emptyList(),
                isLoading = true,
                onCreditClick = onCreditClick,
                onCreateCredit = onCreateCredit,
                onTakeCredit = { rateId, sum, bankAccountNum ->
                    viewModel.takeCredit(
                        rateId,
                        sum,
                        bankAccountNum
                    )
                },
                modifier = Modifier.padding(paddingValues)
            )

            is CreditsScreenState.Error -> ErrorCreditsScreen(state.message)
        }
    }
}

@Composable
private fun DefaultCreditsScreen(
    credits: List<Credit>,
    creditRates: List<CreditRate>,
    isLoading: Boolean,
    onCreditClick: (String) -> Unit,
    onCreateCredit: () -> Unit,
    onTakeCredit: (UUID, Double, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val totalDebt = credits.sumOf { it.debt }
    val listState = rememberLazyListState()
    var scrollToRates by remember { mutableStateOf(false) }

    LaunchedEffect(scrollToRates) {
        if (scrollToRates && creditRates.isNotEmpty()) {
            val ratesStartIndex = 4 + credits.size
            if (ratesStartIndex < listState.layoutInfo.totalItemsCount) {
                listState.animateScrollToItem(ratesStartIndex)
            }
            scrollToRates = false
        }
    }

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Text(
                text = "Кредиты",
                style = MaterialTheme.typography.headlineMedium
            )
        }

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
                        text = "Общая задолженность",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = totalDebt.formatMoney(),
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.CreditCard,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${credits.size} ${if (credits.size == 1) "кредит" else "кредита"}",
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
                    text = "Мои кредиты",
                    style = MaterialTheme.typography.headlineSmall
                )
                FilledIconButton(
                    onClick = {
                        if (creditRates.isNotEmpty()) {
                            scrollToRates = true
                        } else {
                            onCreateCredit()
                        }
                    },
                    modifier = Modifier.size(40.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Оформить кредит",
                        tint = Color.White
                    )
                }
            }
        }

        if (isLoading && credits.isEmpty()) {
            items(3) {
                CreditCardShimmer()
            }
        } else if (credits.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Face,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = BankColors.MediumGray.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "У вас пока нет кредитов",
                        color = BankColors.MediumGray
                    )
                    Text(
                        text = "Нажмите \"+\" чтобы оформить",
                        style = MaterialTheme.typography.bodySmall,
                        color = BankColors.MediumGray
                    )
                }
            }
        } else {
            items(credits) { credit ->
                if (isLoading) {
                    CreditCardShimmer()
                } else {
                    CreditCard(
                        credit = credit,
                        onClick = { onCreditClick(credit.id) }
                    )
                }
            }
        }
        
        if (creditRates.isNotEmpty()) {
            item {
                Text(
                    text = "Доступные программы",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
            
            items(creditRates) { rate ->
                CreditRateItem(
                    rate = rate,
                    onTakeCredit = onTakeCredit
                )
            }
        }
    }
}

@Composable
private fun LoadingCreditsScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun CreditCardShimmer() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .width(120.dp)
                            .height(16.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(BankColors.LightGray)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .width(150.dp)
                            .height(24.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(BankColors.LightGray)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .width(100.dp)
                            .height(14.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(BankColors.LightGray)
                    )
                }
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(BankColors.LightGray)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(BankColors.LightGray)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .width(80.dp)
                        .height(12.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(BankColors.LightGray)
                )
                Box(
                    modifier = Modifier
                        .width(90.dp)
                        .height(12.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(BankColors.LightGray)
                )
            }
        }
    }
}

@Composable
private fun CreditRateItem(
    rate: CreditRate,
    onTakeCredit: (UUID, Double, String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var amount by remember { mutableStateOf("") }
    var bankAccountNum by remember { mutableStateOf("") }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showDialog = true },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = rate.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Процентная ставка",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${rate.percent}%",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Column {
                    Text(
                        text = "Срок",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = rate.writeOffPeriod,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
    
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "Оформить кредит") },
            text = {
                Column {
                    Text(text = "Введите сумму кредита:")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        label = { Text("Сумма") },
                        keyboardOptions = KeyboardOptions.Default
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Введите номер банковского счета:")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = bankAccountNum,
                        onValueChange = { bankAccountNum = it },
                        label = { Text("Номер счета") },
                        keyboardOptions = KeyboardOptions.Default
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (amount.isNotBlank() && bankAccountNum.isNotBlank()) {
                            val amountValue = amount.toDoubleOrNull()
                            if (amountValue != null) {
                                onTakeCredit(rate.rateId, amountValue, bankAccountNum)
                                showDialog = false
                                amount = ""
                                bankAccountNum = ""
                            }
                        }
                    },
                    enabled = amount.isNotBlank() && bankAccountNum.isNotBlank()
                ) {
                    Text("Оформить")
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showDialog = false
                    amount = ""
                    bankAccountNum = ""
                }) {
                    Text("Отмена")
                }
            }
        )
    }
}

@Composable
private fun ErrorCreditsScreen(message: String) {
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
fun CreditsScreenPreview() {
    GbankForClientTheme {
        DefaultCreditsScreen(
            credits = listOf(
                Credit(
                    id = "1",
                    name = "Потребительский кредит",
                    amount = 300000.0,
                    debt = 245000.0,
                    interestRate = 14.9
                ),
                Credit(
                    id = "2",
                    name = "Автокредит",
                    amount = 1200000.0,
                    debt = 980000.0,
                    interestRate = 9.5
                )
            ),
            creditRates = listOf(
                CreditRate(
                    rateId = UUID.randomUUID(),
                    name = "Потребительский кредит",
                    percent = 14,
                    writeOffPeriod = "P60M"
                ),
                CreditRate(
                    rateId = UUID.randomUUID(),
                    name = "Ипотека",
                    percent = 8,
                    writeOffPeriod = "P120M"
                )
            ),
            onCreditClick = { },
            onCreateCredit = { },
            onTakeCredit = { _, _, _ -> },
            isLoading = false,
        )
    }
}
