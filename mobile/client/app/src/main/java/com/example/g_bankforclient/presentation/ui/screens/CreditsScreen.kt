package com.example.g_bankforclient.presentation.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.g_bankforclient.domain.models.Account
import com.example.g_bankforclient.domain.models.Credit
import com.example.g_bankforclient.domain.models.CreditRate
import com.example.g_bankforclient.domain.models.CreditRating
import com.example.g_bankforclient.domain.models.CreditRatingLevel
import com.example.g_bankforclient.presentation.state.CreditsScreenState
import com.example.g_bankforclient.presentation.ui.components.CreditCard
import com.example.g_bankforclient.presentation.ui.components.ErrorDialog
import com.example.g_bankforclient.presentation.ui.utils.formatMoney
import com.example.g_bankforclient.presentation.viewmodel.CreditsViewModel
import com.example.g_bankforclient.ui.theme.BankColors
import com.example.g_bankforclient.ui.theme.GbankForClientTheme
import java.util.UUID

@Composable
fun CreditsScreen(
    onCreditClick: (String) -> Unit,
    onCreateCredit: () -> Unit,
    onMissedPayments: () -> Unit = {}
) {
    val viewModel: CreditsViewModel = hiltViewModel()
    val screenState by viewModel.state.collectAsStateWithLifecycle()
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

    DisposableEffect(Unit) {
        onDispose { viewModel.dismissRatingDialog() }
    }

    val defaultState = screenState as? CreditsScreenState.Default
    if (defaultState?.errorMessage != null) {
        ErrorDialog(
            message = defaultState.errorMessage,
            onDismiss = { viewModel.clearError() }
        )
    }

    val errorState = screenState as? CreditsScreenState.Error
    if (errorState != null) {
        ErrorDialog(
            message = errorState.message,
            onDismiss = { viewModel.loadCreditsAndRates() },
            onRetry = { viewModel.loadCreditsAndRates() }
        )
    }

    val rubAccounts = viewModel.rubAccounts

    Scaffold { paddingValues ->
        when (val state = screenState) {
            is CreditsScreenState.Default -> DefaultCreditsScreen(
                credits = state.credits,
                creditRates = state.creditRates,
                rubAccounts = rubAccounts,
                creditRating = state.creditRating,
                showRatingDialog = state.showRatingDialog,
                isRatingLoading = state.isRatingLoading,
                isLoading = state.isLoading,
                onCreditClick = onCreditClick,
                onCreateCredit = onCreateCredit,
                onTakeCredit = { rateId, sum, bankAccountNum ->
                    viewModel.takeCredit(rateId, sum, bankAccountNum)
                },
                onShowRating = { viewModel.loadCreditRating() },
                onDismissRating = { viewModel.dismissRatingDialog() },
                onMissedPayments = onMissedPayments,
                modifier = Modifier.padding(paddingValues)
            )

            CreditsScreenState.Loading -> DefaultCreditsScreen(
                credits = emptyList(),
                creditRates = emptyList(),
                rubAccounts = rubAccounts,
                creditRating = null,
                showRatingDialog = false,
                isRatingLoading = false,
                isLoading = true,
                onCreditClick = onCreditClick,
                onCreateCredit = onCreateCredit,
                onTakeCredit = { _, _, _ -> },
                onShowRating = {},
                onDismissRating = {},
                onMissedPayments = onMissedPayments,
                modifier = Modifier.padding(paddingValues)
            )

            is CreditsScreenState.Error -> DefaultCreditsScreen(
                credits = emptyList(),
                creditRates = emptyList(),
                rubAccounts = rubAccounts,
                creditRating = null,
                showRatingDialog = false,
                isRatingLoading = false,
                isLoading = false,
                onCreditClick = {},
                onCreateCredit = {},
                onTakeCredit = { _, _, _ -> },
                onShowRating = {},
                onDismissRating = {},
                onMissedPayments = {},
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}

@Composable
private fun DefaultCreditsScreen(
    credits: List<Credit>,
    creditRates: List<CreditRate>,
    rubAccounts: List<Account>,
    creditRating: CreditRating?,
    showRatingDialog: Boolean,
    isRatingLoading: Boolean,
    isLoading: Boolean,
    onCreditClick: (String) -> Unit,
    onCreateCredit: () -> Unit,
    onTakeCredit: (UUID, Double, String) -> Unit,
    onShowRating: () -> Unit,
    onDismissRating: () -> Unit,
    onMissedPayments: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val totalDebt = credits.sumOf { it.debt }
    val listState = rememberLazyListState()
    var scrollToRates by remember { mutableStateOf(false) }

    if (showRatingDialog && creditRating != null) {
        CreditRatingDialog(
            rating = creditRating,
            onDismiss = onDismissRating
        )
    }

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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Кредиты",
                    style = MaterialTheme.typography.headlineMedium
                )
                Row {
                    IconButton(onClick = onMissedPayments) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Просроченные платежи",
                            tint = BankColors.ErrorRed
                        )
                    }
                    IconButton(
                        onClick = { onShowRating() }
                    ) {
                        if (isRatingLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Кредитный рейтинг",
                                tint = when (creditRating?.level) {
                                    CreditRatingLevel.EXCELLENT -> Color(0xFF4CAF50)
                                    CreditRatingLevel.GOOD -> Color(0xFF8BC34A)
                                    CreditRatingLevel.FAIR -> Color(0xFFFF9800)
                                    CreditRatingLevel.POOR -> BankColors.ErrorRed
                                    null -> BankColors.MediumGray
                                }
                            )
                        }
                    }
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
                    rubAccounts = rubAccounts,
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
    rubAccounts: List<Account>,
    onTakeCredit: (UUID, Double, String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var amount by remember { mutableStateOf("") }
    var selectedAccountId by remember { mutableStateOf(rubAccounts.firstOrNull()?.id ?: "") }

    LaunchedEffect(rubAccounts) {
        if (selectedAccountId.isEmpty() && rubAccounts.isNotEmpty()) {
            selectedAccountId = rubAccounts.first().id
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showDialog = true },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant
        )
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
        val selectedAccount = rubAccounts.find { it.id == selectedAccountId }
        var isAccountsExpanded by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = {
                showDialog = false
                amount = ""
                isAccountsExpanded = false
            },
            title = { Text(text = rate.name) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column {
                        Text(
                            text = "Зачислить на счёт",
                            style = MaterialTheme.typography.bodySmall,
                            color = BankColors.SecondaryText
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        if (rubAccounts.isEmpty()) {
                            Text(
                                text = "Нет доступных рублёвых счетов",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                        } else {
                            selectedAccount?.let { account ->
                                Surface(
                                    onClick = {
                                        if (rubAccounts.size > 1) {
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
                                            .padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "${account.name} — ${
                                                account.balance.formatMoney(
                                                    "₽"
                                                )
                                            }",
                                            style = MaterialTheme.typography.bodyMedium,
                                            modifier = Modifier.weight(1f)
                                        )
                                        if (rubAccounts.size > 1) {
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
                            if (rubAccounts.size > 1) {
                                AnimatedVisibility(
                                    visible = isAccountsExpanded,
                                    enter = expandVertically() + fadeIn(),
                                    exit = shrinkVertically() + fadeOut()
                                ) {
                                    Column {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        rubAccounts
                                            .filter { it.id != selectedAccountId }
                                            .forEach { account ->
                                                Surface(
                                                    onClick = {
                                                        selectedAccountId = account.id
                                                        isAccountsExpanded = false
                                                    },
                                                    modifier = Modifier.fillMaxWidth(),
                                                    shape = RoundedCornerShape(12.dp),
                                                    color = BankColors.LightGray
                                                ) {
                                                    Text(
                                                        text = "${account.name} — ${
                                                            account.balance.formatMoney(
                                                                "₽"
                                                            )
                                                        }",
                                                        modifier = Modifier.padding(12.dp),
                                                        style = MaterialTheme.typography.bodyMedium
                                                    )
                                                }
                                                Spacer(modifier = Modifier.height(4.dp))
                                            }
                                    }
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        label = { Text("Сумма кредита (₽)") },
                        placeholder = { Text("0") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amountValue = amount.toDoubleOrNull()
                        if (amountValue != null && amountValue > 0 && selectedAccountId.isNotBlank()) {
                            onTakeCredit(rate.rateId, amountValue, selectedAccountId)
                            showDialog = false
                            amount = ""
                            isAccountsExpanded = false
                        }
                    },
                    enabled = amount.toDoubleOrNull().let { it != null && it > 0 }
                            && selectedAccountId.isNotBlank()
                            && rubAccounts.isNotEmpty()
                ) {
                    Text("Оформить")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDialog = false
                    amount = ""
                    isAccountsExpanded = false
                }) {
                    Text("Отмена")
                }
            }
        )
    }
}

@Composable
private fun CreditRatingDialog(
    rating: CreditRating,
    onDismiss: () -> Unit
) {
    val (levelLabel, levelColor) = when (rating.level) {
        CreditRatingLevel.EXCELLENT -> "Отличный" to Color(0xFF4CAF50)
        CreditRatingLevel.GOOD -> "Хороший" to Color(0xFF8BC34A)
        CreditRatingLevel.FAIR -> "Удовлетворительный" to Color(0xFFFF9800)
        CreditRatingLevel.POOR -> "Низкий" to Color(0xFFF44336)
    }

    val scoreProgress = ((rating.score - 1f) / (999f - 1f)).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = scoreProgress,
        animationSpec = tween(durationMillis = 1000),
        label = "scoreArc"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Кредитный рейтинг",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier.size(160.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val strokeWidth = 16.dp.toPx()
                        val startAngle = 180f
                        val sweepAngle = 180f

                        drawArc(
                            color = Color.LightGray.copy(alpha = 0.3f),
                            startAngle = startAngle,
                            sweepAngle = sweepAngle,
                            useCenter = false,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                        drawArc(
                            color = levelColor,
                            startAngle = startAngle,
                            sweepAngle = sweepAngle * animatedProgress,
                            useCenter = false,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(top = 32.dp)
                    ) {
                        Text(
                            text = "${rating.score}",
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold,
                            color = levelColor
                        )
                        Text(
                            text = levelLabel,
                            style = MaterialTheme.typography.labelMedium,
                            color = levelColor
                        )
                        Text(
                            text = "из 999",
                            style = MaterialTheme.typography.labelSmall,
                            color = BankColors.SecondaryText
                        )
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        RatingStatRow("Всего кредитов", "${rating.totalCredits}")
                        RatingStatRow(
                            "Закрытых кредитов",
                            "${rating.closedCredits}",
                            Color(0xFF4CAF50)
                        )
                        RatingStatRow("Активных кредитов", "${rating.activeCredits}")
                        RatingStatRow(
                            label = "Просроченных платежей",
                            value = "${rating.missedPayments}",
                            valueColor = if (rating.missedPayments > 0) Color(0xFFF44336) else Color(
                                0xFF4CAF50
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Закрыть")
            }
        }
    )
}

@Composable
private fun RatingStatRow(
    label: String,
    value: String,
    valueColor: Color = Color.Unspecified
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = BankColors.SecondaryText
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = valueColor
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
            rubAccounts = listOf(
                Account(id = "ACC-001", name = "Текущий счёт", balance = 50000.0)
            ),
            onCreditClick = { },
            onCreateCredit = { },
            onTakeCredit = { _, _, _ -> },
            onShowRating = {},
            onDismissRating = {},
            creditRating = null,
            showRatingDialog = false,
            isRatingLoading = false,
            isLoading = false,
        )
    }
}
