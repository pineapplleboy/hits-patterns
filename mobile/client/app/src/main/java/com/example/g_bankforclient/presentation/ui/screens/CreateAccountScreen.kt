package com.example.g_bankforclient.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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

    when (val state = screenState) {
        CreateAccountScreenState.Default -> DefaultCreateAccountScreen(
            onBack = onBack,
            onCreateAccount = { viewModel.createAccount() }
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
    onBack: () -> Unit,
    onCreateAccount: () -> Unit
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
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

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

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Новый банковский счёт",
                style = MaterialTheme.typography.titleLarge
            )

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = BankColors.LightGray.copy(alpha = 0.5f)
            ) {
                Text(
                    text = "Счёт будет создан с нулевым балансом. Вы сможете пополнить его после создания.",
                    style = MaterialTheme.typography.bodySmall,
                    color = BankColors.SecondaryText,
                    modifier = Modifier.padding(16.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onCreateAccount,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
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
            onBack = {},
            onCreateAccount = {}
        )
    }
}
