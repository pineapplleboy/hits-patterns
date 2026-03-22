package com.example.g_bankforclient.presentation.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.g_bankforclient.domain.models.Transaction
import com.example.g_bankforclient.presentation.state.MissedPaymentsScreenState
import com.example.g_bankforclient.presentation.ui.components.ErrorDialog
import com.example.g_bankforclient.presentation.ui.components.LoadingContent
import com.example.g_bankforclient.presentation.ui.components.TransactionItem
import com.example.g_bankforclient.presentation.viewmodel.MissedPaymentsViewModel
import com.example.g_bankforclient.ui.theme.BankColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MissedPaymentsScreen(
    onBack: () -> Unit
) {
    val viewModel: MissedPaymentsViewModel = hiltViewModel()
    val screenState by viewModel.state.collectAsStateWithLifecycle()

    val errorState = screenState as? MissedPaymentsScreenState.Error
    if (errorState != null) {
        ErrorDialog(
            message = errorState.message,
            onDismiss = onBack,
            onRetry = { viewModel.load() }
        )
    }

    when (val state = screenState) {
        is MissedPaymentsScreenState.Default -> DefaultMissedPaymentsScreen(
            payments = state.payments,
            onBack = onBack
        )

        MissedPaymentsScreenState.Loading -> LoadingContent()
        is MissedPaymentsScreenState.Error -> LoadingContent()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DefaultMissedPaymentsScreen(
    payments: List<Transaction>,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 0.dp
            ) {
                Column {
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
                        text = "Просроченные платежи",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
                    )
                }
            }
        }
    ) { paddingValues ->
        if (payments.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = BankColors.MediumGray.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Просроченных платежей нет",
                    color = BankColors.MediumGray
                )
                Text(
                    text = "Все платежи вовремя",
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
                item {
                    Text(
                        text = "Найдено просроченных: ${payments.size}",
                        style = MaterialTheme.typography.bodySmall,
                        color = BankColors.ErrorRed
                    )
                }
                items(payments) { payment ->
                    TransactionItem(transaction = payment)
                }
            }
        }
    }
}

