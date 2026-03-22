package com.example.g_bankforemployees.feature.settings.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.g_bankforemployees.R
import com.example.g_bankforemployees.common.presentation.component.BankTopBar
import com.example.g_bankforemployees.common.presentation.component.ErrorState
import com.example.g_bankforemployees.common.presentation.component.LoadingState
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    SettingsScreenContent(
        state = state,
        onBackClick = viewModel::onBackClick,
        onDarkThemeToggle = viewModel::onDarkThemeToggle,
        onRetry = {},
    )
}

@Composable
private fun SettingsScreenContent(
    state: SettingsScreenState,
    onBackClick: () -> Unit,
    onDarkThemeToggle: (Boolean) -> Unit,
    onRetry: () -> Unit,
) {
    when (state) {
        SettingsScreenState.Loading -> LoadingState()
        is SettingsScreenState.Error -> ErrorState(
            title = stringResource(R.string.error),
            description = state.message,
            onRetry = onRetry,
        )
        is SettingsScreenState.Default -> Scaffold(
            topBar = {
                BankTopBar(
                    title = stringResource(R.string.settings),
                    onBackClick = onBackClick,
                )
            },
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.dark_theme),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f),
                    )
                    Switch(
                        checked = state.isDarkTheme,
                        onCheckedChange = onDarkThemeToggle,
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun SettingsScreenPreview() = SettingsScreen(viewModel = koinViewModel())
