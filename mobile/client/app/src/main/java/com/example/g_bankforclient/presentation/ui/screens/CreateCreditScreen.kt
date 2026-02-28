package com.example.g_bankforclient.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.g_bankforclient.presentation.state.CreateCreditScreenState
import com.example.g_bankforclient.presentation.viewmodel.CreateCreditViewModel
import com.example.g_bankforclient.ui.theme.BankColors
import com.example.g_bankforclient.ui.theme.GbankForClientTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCreditScreen(
    onBack: () -> Unit,
    onCreditCreated: () -> Unit
) {
    val viewModel: CreateCreditViewModel = hiltViewModel()
    val screenState by viewModel.state.collectAsStateWithLifecycle()
    var name by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var interestRate by remember { mutableStateOf("") }

    val isValid = name.isNotBlank() &&
            (amount.toDoubleOrNull() ?: 0.0) > 0 &&
            (interestRate.toDoubleOrNull() ?: 0.0) > 0

    when (val state = screenState) {
        CreateCreditScreenState.Default -> DefaultCreateCreditScreen(
            name = name,
            amount = amount,
            interestRate = interestRate,
            isValid = isValid,
            onNameChange = { name = it },
            onAmountChange = { amount = it },
            onInterestRateChange = { interestRate = it },
            onBack = onBack,
            onCreateCredit = { n, a, r -> viewModel.createCredit(n, a, r) }
        )
        
        CreateCreditScreenState.Loading -> LoadingCreateCreditScreen()
        
        is CreateCreditScreenState.Success -> {
            LaunchedEffect(Unit) {
                onCreditCreated()
            }
            SuccessCreateCreditScreen(state.message)
        }
        
        is CreateCreditScreenState.Error -> ErrorCreateCreditScreen(state.message)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DefaultCreateCreditScreen(
    name: String,
    amount: String,
    interestRate: String,
    isValid: Boolean,
    onNameChange: (String) -> Unit,
    onAmountChange: (String) -> Unit,
    onInterestRateChange: (String) -> Unit,
    onBack: () -> Unit,
    onCreateCredit: (String, Double, Double) -> Unit
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
                        text = "Оформить кредит",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Icon
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(128.dp)
                        .clip(CircleShape)
                        .background(BankColors.ErrorRed.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.CreditCard,
                        contentDescription = null,
                        tint = BankColors.ErrorRed,
                        modifier = Modifier.size(64.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Form
            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                label = { Text("Название кредита") },
                placeholder = { Text("Например: Ипотека") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = amount,
                onValueChange = onAmountChange,
                label = { Text("Сумма кредита (₽)") },
                placeholder = { Text("0") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = interestRate,
                onValueChange = onInterestRateChange,
                label = { Text("Процентная ставка (%)") },
                placeholder = { Text("0") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = BankColors.LightGray.copy(alpha = 0.5f)
            ) {
                Text(
                    text = "После оформления кредита средства будут доступны для использования. Вы сможете погашать кредит со своих счетов.",
                    style = MaterialTheme.typography.bodySmall,
                    color = BankColors.SecondaryText,
                    modifier = Modifier.padding(16.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    val amountValue = amount.toDoubleOrNull()
                    val rateValue = interestRate.toDoubleOrNull()
                    if (isValid && amountValue != null && rateValue != null) {
                        onCreateCredit(name.trim(), amountValue, rateValue)
                    }
                },
                enabled = isValid,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Оформить кредит")
            }
        }
    }
}

@Composable
private fun LoadingCreateCreditScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text("Оформление кредита...")
        }
    }
}

@Composable
private fun SuccessCreateCreditScreen(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}

@Composable
private fun ErrorCreateCreditScreen(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Error,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Ошибка",
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
fun CreateCreditScreenPreview() {
    GbankForClientTheme {
        DefaultCreateCreditScreen(
            name = "",
            amount = "",
            interestRate = "",
            isValid = false,
            onNameChange = { /* Заглушка */ },
            onAmountChange = { /* Заглушка */ },
            onInterestRateChange = { /* Заглушка */ },
            onBack = { /* Заглушка */ },
            onCreateCredit = { _, _, _ -> /* Заглушка */ }
        )
    }
}
