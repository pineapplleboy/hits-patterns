package com.example.g_bankforclient.presentation.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.g_bankforclient.domain.models.Account
import com.example.g_bankforclient.domain.models.ExchangeRate
import com.example.g_bankforclient.presentation.state.AccountDetailsScreenState
import com.example.g_bankforclient.presentation.ui.components.ErrorDialog
import com.example.g_bankforclient.presentation.ui.components.LoadingContent
import com.example.g_bankforclient.presentation.ui.utils.formatMoney
import com.example.g_bankforclient.presentation.viewmodel.AccountDetailsViewModel
import com.example.g_bankforclient.ui.theme.BankColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountDetailsScreen(
    accountId: String,
    isHidden: Boolean = false,
    onBack: () -> Unit,
    onViewHistory: () -> Unit,
    onAccountClosed: () -> Unit
) {
    val viewModel: AccountDetailsViewModel = hiltViewModel()
    val screenState by viewModel.state.collectAsStateWithLifecycle()
    var amount by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(accountId) {
        viewModel.loadAccountDetails(accountId, isHidden)
    }

    LaunchedEffect(screenState) {
        val s = screenState as? AccountDetailsScreenState.Default
        val msg = s?.snackbarMessage
        if (msg != null) {
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    val defaultState = screenState as? AccountDetailsScreenState.Default
    if (defaultState?.errorMessage != null) {
        ErrorDialog(
            message = defaultState.errorMessage,
            onDismiss = { viewModel.clearError() }
        )
    }

    val errorState = screenState as? AccountDetailsScreenState.Error
    if (errorState != null) {
        ErrorDialog(
            message = errorState.message,
            onDismiss = onBack,
            onRetry = { viewModel.loadAccountDetails(accountId) }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        when (val state = screenState) {
            is AccountDetailsScreenState.Default -> DefaultAccountDetailsScreen(
                account = state.account,
                amount = amount,
                showDeleteDialog = showDeleteDialog,
                isLoading = state.isLoading,
                myAccounts = state.myAccounts,
                exchangeRates = state.exchangeRates,
                onAmountChange = { amount = it },
                onShowDeleteDialogChange = { showDeleteDialog = it },
                onBack = onBack,
                onToggleVisibility = {
                    if (state.account.uuid.isNotBlank()) {
                        viewModel.toggleVisibility(state.account.uuid)
                    }
                },
                onDeposit = { viewModel.deposit(state.account.id, it) },
                onWithdrawal = { viewModel.withdrawal(state.account.id, it) },
                onTransfer = { toAccount, transferAmount ->
                    viewModel.transfer(state.account.id, toAccount, transferAmount)
                },
                onViewHistory = onViewHistory,
                onCloseAccount = {
                    viewModel.closeAccount(state.account.id)
                    onAccountClosed()
                },
                modifier = Modifier.padding(paddingValues)
            )

            AccountDetailsScreenState.Loading -> LoadingContent()

            is AccountDetailsScreenState.Error -> LoadingContent()
        }
    }
}

@Composable
fun ErrorAccountDetailsScreen(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DefaultAccountDetailsScreen(
    account: Account,
    amount: String,
    showDeleteDialog: Boolean,
    isLoading: Boolean,
    myAccounts: List<Account>,
    exchangeRates: Map<String, ExchangeRate>,
    onAmountChange: (String) -> Unit,
    onShowDeleteDialogChange: (Boolean) -> Unit,
    onBack: () -> Unit,
    onToggleVisibility: () -> Unit,
    onDeposit: (Double) -> Unit,
    onWithdrawal: (Double) -> Unit,
    onTransfer: (toAccount: String, amount: Double) -> Unit,
    onViewHistory: () -> Unit,
    onCloseAccount: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showTransferOwnDialog by remember { mutableStateOf(false) }
    var showTransferExternalDialog by remember { mutableStateOf(false) }

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
                            onClick = onToggleVisibility,
                            modifier = Modifier.padding(end = 8.dp),
                            enabled = !isLoading
                        ) {
                            Icon(
                                imageVector = if (account.isHidden) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (account.isHidden) "Показать счёт" else "Скрыть счёт",
                                tint = if (isLoading) BankColors.MediumGray.copy(alpha = 0.5f) else BankColors.MediumGray
                            )
                        }
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
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        BankColors.PurplePrimary,
                                        BankColors.PurplePrimaryLight
                                    )
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
                                text = account.balanceDisplay
                                    ?: account.balance.formatMoney(account.currencySymbol ?: "₽"),
                                style = MaterialTheme.typography.displaySmall,
                                color = Color.White
                            )
                            if (account.isHidden) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Счёт скрыт",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }

                if (account.banned) {
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = BankColors.ErrorRed.copy(alpha = 0.1f),
                            border = BorderStroke(1.dp, BankColors.ErrorRed.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Block,
                                    contentDescription = null,
                                    tint = BankColors.ErrorRed,
                                    modifier = Modifier.size(24.dp)
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Счёт заблокирован",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = BankColors.ErrorRed
                                    )
                                    Text(
                                        text = "Операции по счёту недоступны",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = BankColors.ErrorRed.copy(alpha = 0.8f)
                                    )
                                }
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
                                    enabled = !isLoading && !account.banned,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(56.dp),
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
                                    enabled = !isLoading && !account.banned,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(56.dp),
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
                            Text(
                                text = "Переводы",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { showTransferOwnDialog = true },
                                    enabled = !isLoading && !account.banned && myAccounts.isNotEmpty(),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(56.dp),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.SwapHoriz,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Свои счета")
                                }
                                OutlinedButton(
                                    onClick = { showTransferExternalDialog = true },
                                    enabled = !isLoading && !account.banned,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(56.dp),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.ArrowForward,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("По номеру")
                                }
                            }
                        }
                    }
                }

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
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = BankColors.ErrorRed
                        ),
                        border = BorderStroke(1.dp, BankColors.ErrorRed.copy(alpha = 0.2f)),
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

        if (showTransferOwnDialog) {
            TransferOwnAccountDialog(
                account = account,
                myAccounts = myAccounts,
                exchangeRates = exchangeRates,
                onDismiss = { showTransferOwnDialog = false },
                onTransfer = { toAccountId, transferAmount ->
                    onTransfer(toAccountId, transferAmount)
                    showTransferOwnDialog = false
                }
            )
        }

        if (showTransferExternalDialog) {
            TransferExternalDialog(
                account = account,
                onDismiss = { showTransferExternalDialog = false },
                onTransfer = { toAccountId, transferAmount ->
                    onTransfer(toAccountId, transferAmount)
                    showTransferExternalDialog = false
                }
            )
        }
    }
}

@Composable
private fun TransferOwnAccountDialog(
    account: Account,
    myAccounts: List<Account>,
    exchangeRates: Map<String, ExchangeRate>,
    onDismiss: () -> Unit,
    onTransfer: (toAccountId: String, amount: Double) -> Unit
) {
    var selectedAccount by remember { mutableStateOf<Account?>(null) }
    var amountStr by remember { mutableStateOf("") }
    var showDropdown by remember { mutableStateOf(false) }

    val fromCode = account.currencyCharCode ?: "RUB"
    val toCode = selectedAccount?.currencyCharCode ?: "RUB"
    val needsConversion = fromCode != toCode

    val enteredAmount = amountStr.toDoubleOrNull() ?: 0.0
    val convertedAmount = if (needsConversion && enteredAmount > 0) {
        convertCurrency(enteredAmount, fromCode, toCode, exchangeRates)
    } else null

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        title = { Text("Перевод на свой счёт") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDropdown = true },
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = selectedAccount?.name ?: "Выберите счёт",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (selectedAccount == null) BankColors.MediumGray else MaterialTheme.colorScheme.onSurface
                        )
                        Icon(
                            Icons.Default.ArrowForward,
                            contentDescription = null,
                            tint = BankColors.MediumGray
                        )
                    }
                    DropdownMenu(
                        expanded = showDropdown,
                        onDismissRequest = { showDropdown = false }
                    ) {
                        myAccounts.forEach { acc ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(acc.name, style = MaterialTheme.typography.bodyMedium)
                                        Text(
                                            acc.balanceDisplay ?: acc.balance.formatMoney(
                                                acc.currencySymbol ?: "₽"
                                            ),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = BankColors.MediumGray
                                        )
                                    }
                                },
                                onClick = {
                                    selectedAccount = acc
                                    showDropdown = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = { Text("Сумма (${account.currencySymbol ?: "₽"})") },
                    placeholder = { Text("0") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                if (needsConversion && convertedAmount != null && convertedAmount > 0) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                    ) {
                        Text(
                            text = "≈ ${convertedAmount.formatMoney(selectedAccount?.currencySymbol ?: "₽")} по курсу ЦБ",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val amt = amountStr.toDoubleOrNull() ?: return@TextButton
                    val toAcc = selectedAccount ?: return@TextButton
                    if (amt <= 0) return@TextButton
                    onTransfer(toAcc.id, amt)
                },
                enabled = selectedAccount != null && (amountStr.toDoubleOrNull() ?: 0.0) > 0
            ) {
                Text("Перевести")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Отмена") }
        }
    )
}

@Composable
private fun TransferExternalDialog(
    account: Account,
    onDismiss: () -> Unit,
    onTransfer: (toAccountId: String, amount: Double) -> Unit
) {
    var toAccountNumber by remember { mutableStateOf("") }
    var amountStr by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        title = { Text("Перевод по номеру счёта") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = toAccountNumber,
                    onValueChange = { toAccountNumber = it },
                    label = { Text("Номер счёта получателя") },
                    placeholder = { Text("Введите номер счёта") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = { Text("Сумма (${account.currencySymbol ?: "₽"})") },
                    placeholder = { Text("0") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val amt = amountStr.toDoubleOrNull() ?: return@TextButton
                    if (amt <= 0 || toAccountNumber.isBlank()) return@TextButton
                    onTransfer(toAccountNumber.trim(), amt)
                },
                enabled = toAccountNumber.isNotBlank() && (amountStr.toDoubleOrNull() ?: 0.0) > 0
            ) {
                Text("Перевести")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Отмена") }
        }
    )
}

/**
 * Конвертирует [amount] из валюты [fromCode] в валюту [toCode] через рубли.
 * Возвращает null если курс недоступен.
 */
private fun convertCurrency(
    amount: Double,
    fromCode: String,
    toCode: String,
    rates: Map<String, ExchangeRate>
): Double? {
    if (fromCode == toCode) return amount
    val amountInRub = when (fromCode) {
        "RUB" -> amount
        else -> {
            val rate = rates[fromCode] ?: return null
            amount * rate.valueRub / rate.nominal
        }
    }
    return when (toCode) {
        "RUB" -> amountInRub
        else -> {
            val rate = rates[toCode] ?: return null
            amountInRub * rate.nominal / rate.valueRub
        }
    }
}
