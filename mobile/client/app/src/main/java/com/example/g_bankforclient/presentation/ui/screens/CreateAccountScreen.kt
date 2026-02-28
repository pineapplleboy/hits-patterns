package com.example.g_bankforclient.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.g_bankforclient.presentation.state.CreateAccountScreenState
import com.example.g_bankforclient.presentation.viewmodel.CreateAccountViewModel
import com.example.g_bankforclient.ui.theme.BankColors
import com.example.g_bankforclient.ui.theme.GbankForClientTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAccountScreen(
    onBack: () -> Unit,
    onAccountCreated: () -> Unit
) {
    val viewModel: CreateAccountViewModel = hiltViewModel()
    val screenState by viewModel.state.collectAsStateWithLifecycle()
    var name by remember { mutableStateOf("") }

    when (val state = screenState) {
        CreateAccountScreenState.Default -> DefaultCreateAccountScreen(
            name = name,
            onNameChange = { name = it },
            onBack = onBack,
            onCreateAccount = { viewModel.createAccount(it) }
        )
        
        CreateAccountScreenState.Loading -> LoadingCreateAccountScreen()
        
        is CreateAccountScreenState.Success -> {
            LaunchedEffect(Unit) {
                onAccountCreated()
            }
            SuccessCreateAccountScreen(state.message)
        }
        
        is CreateAccountScreenState.Error -> ErrorCreateAccountScreen(state.message)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DefaultCreateAccountScreen(
    name: String,
    onNameChange: (String) -> Unit,
    onBack: () -> Unit,
    onCreateAccount: (String) -> Unit
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
                        text = "Открыть счет",
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
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.AccountBalance,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(64.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Form
            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                label = { Text("Название счета") },
                placeholder = { Text("Например: Основной счет") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = BankColors.LightGray.copy(alpha = 0.5f)
            ) {
                Text(
                    text = "Счет будет создан с нулевым балансом. Вы сможете пополнить его после создания.",
                    style = MaterialTheme.typography.bodySmall,
                    color = BankColors.SecondaryText,
                    modifier = Modifier.padding(16.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onCreateAccount(name.trim())
                    }
                },
                enabled = name.isNotBlank(),
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Открыть счет")
            }
        }
    }
}

@Composable
private fun LoadingCreateAccountScreen() {
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
            Text("Создание счета...")
        }
    }
}

@Composable
private fun SuccessCreateAccountScreen(message: String) {
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
private fun ErrorCreateAccountScreen(message: String) {
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
fun CreateAccountScreenPreview() {
    GbankForClientTheme {
        DefaultCreateAccountScreen(
            name = "",
            onNameChange = { /* Заглушка */ },
            onBack = { /* Заглушка */ },
            onCreateAccount = { /* Заглушка */ }
        )
    }
}
