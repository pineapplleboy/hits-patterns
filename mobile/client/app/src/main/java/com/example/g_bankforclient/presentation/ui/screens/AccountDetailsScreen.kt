package com.example.g_bankforclient.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
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
import com.example.g_bankforclient.presentation.state.AccountDetailsScreenState
import com.example.g_bankforclient.presentation.viewmodel.AccountDetailsViewModel
import com.example.g_bankforclient.ui.theme.BankColors
import com.example.g_bankforclient.ui.theme.GbankForClientTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountDetailsScreen(
    accountId: String,
    onBack: () -> Unit,
    onViewHistory: () -> Unit,
    onAccountClosed: () -> Unit
) {
    val viewModel: AccountDetailsViewModel = hiltViewModel()
    val screenState by viewModel.state.collectAsStateWithLifecycle()
    var amount by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(accountId) {
        viewModel.loadAccountDetails(accountId)
    }

    when (val state = screenState) {
        is AccountDetailsScreenState.Default -> DefaultAccountDetailsScreen(
            account = state.account,
            amount = amount,
            showDeleteDialog = showDeleteDialog,
            onAmountChange = { amount = it },
            onShowDeleteDialogChange = { showDeleteDialog = it },
            onBack = onBack,
            onDeposit = { viewModel.deposit(state.account.id, it) },
            onWithdrawal = { viewModel.withdrawal(state.account.id, it) },
            onViewHistory = onViewHistory,
            onCloseAccount = { 
                viewModel.closeAccount(state.account.id)
                onAccountClosed()
            }
        )
        
        AccountDetailsScreenState.Loading -> LoadingAccountDetailsScreen()
        
        is AccountDetailsScreenState.Error -> ErrorAccountDetailsScreen(state.message)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DefaultAccountDetailsScreen(
    account: Account,
    amount: String,
    showDeleteDialog: Boolean,
    onAmountChange: (String) -> Unit,
    onShowDeleteDialogChange: (Boolean) -> Unit,
    onBack: () -> Unit,
    onDeposit: (Double) -> Unit,
    onWithdrawal: (Double) -> Unit,
    onViewHistory: () -> Unit,
    onCloseAccount: () -> Unit
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
                        text = account.name,
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
            // Balance Card
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
                            text = "Текущий баланс",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${account.balance.toInt().toString().replace(Regex("(\\d)(?=(\\d{3})+$)"), "$1 ")} ₽",
                            style = MaterialTheme.typography.displaySmall,
                            color = Color.White
                        )
                    }
                }
            }

            // Actions Card
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
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = {
                                    amount.toDoubleOrNull()?.let { value ->
                                        if (value > 0) {
                                            onDeposit(value)
                                            onAmountChange("")
                                        }
                                    }
                                },
                                modifier = Modifier.weight(1f).height(56.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = BankColors.Success
                                ),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.FileDownload,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Внести")
                            }
                            Button(
                                onClick = {
                                    amount.toDoubleOrNull()?.let { value ->
                                        if (value > 0 && value <= account.balance) {
                                            onWithdrawal(value)
                                            onAmountChange("")
                                        }
                                    }
                                },
                                modifier = Modifier.weight(1f).height(56.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = BankColors.Warning
                                ),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.FileUpload,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Снять")
                            }
                        }

                        OutlinedTextField(
                            value = amount,
                            onValueChange = onAmountChange,
                            label = { Text("Сумма операции") },
                            placeholder = { Text("0") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }

            // View History Button
            item {
                Surface(
                    onClick = onViewHistory,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 2.dp,
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
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
                                    .clip(CircleShape)
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
                            Text("История операций")
                        }
                        Icon(
                            imageVector = Icons.Filled.ArrowForward,
                            contentDescription = null,
                            tint = BankColors.MediumGray
                        )
                    }
                }
            }

            item {
                OutlinedButton(
                    onClick = { onShowDeleteDialogChange(true) },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = BankColors.ErrorRed
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BankColors.ErrorRed.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Закрыть счет")
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { onShowDeleteDialogChange(false) },
            title = { Text("Закрыть счет?") },
            text = {
                Text(
                    "Вы уверены, что хотите закрыть счет \"${account.name}\"?" +
                            if (account.balance > 0) " Счет должен иметь нулевой баланс для закрытия." else ""
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (account.balance == 0.0) {
                            onCloseAccount()
                        }
                        onShowDeleteDialogChange(false)
                    }
                ) {
                    Text("Закрыть", color = BankColors.ErrorRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { onShowDeleteDialogChange(false) }) {
                    Text("Отмена")
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }
}

@Composable
private fun LoadingAccountDetailsScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorAccountDetailsScreen(message: String) {
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
fun AccountDetailsScreenPreview() {
    GbankForClientTheme {
        DefaultAccountDetailsScreen(
            account = Account(
                id = "1",
                name = "Основной счёт",
                balance = 123456.78
            ),
            amount = "",
            showDeleteDialog = false,
            onAmountChange = { /* Заглушка */ },
            onShowDeleteDialogChange = { /* Заглушка */ },
            onBack = { /* Заглушка */ },
            onDeposit = { /* Заглушка */ },
            onWithdrawal = { /* Заглушка */ },
            onViewHistory = { /* Заглушка */ },
            onCloseAccount = { /* Заглушка */ }
        )
    }
}
