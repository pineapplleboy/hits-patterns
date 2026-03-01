package com.example.g_bankforemployees.feature.credit_rate.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.g_bankforemployees.R
import com.example.g_bankforemployees.common.presentation.component.BankTopBar
import com.example.g_bankforemployees.common.presentation.component.FormCard

@Composable
fun CreditRateCreateScreen(viewModel: CreditRateCreateScreenViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    CreditRateCreateScreenContent(
        state = state,
        onBackClick = viewModel::onBackClick,
        onNameChange = viewModel::onNameChange,
        onPercentChange = viewModel::onPercentChange,
        onDaysChange = viewModel::onDaysChange,
        onHoursChange = viewModel::onHoursChange,
        onMinutesChange = viewModel::onMinutesChange,
        onCreateRate = viewModel::createRate,
    )
}

@Composable
private fun CreditRateCreateScreenContent(
    state: CreditRateCreateScreenState,
    onBackClick: () -> Unit,
    onNameChange: (String) -> Unit,
    onPercentChange: (String) -> Unit,
    onDaysChange: (String) -> Unit,
    onHoursChange: (String) -> Unit,
    onMinutesChange: (String) -> Unit,
    onCreateRate: () -> Unit,
) {
    Scaffold(
        topBar = {
            BankTopBar(
                title = stringResource(R.string.new_rate),
                onBackClick = onBackClick,
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            FormCard(modifier = Modifier.padding(horizontal = 24.dp)) {
                Text(
                    text = stringResource(R.string.new_credit_rate_title),
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                OutlinedTextField(
                    value = state.name,
                    onValueChange = onNameChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.tariff_name)) },
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = state.percent,
                    onValueChange = onPercentChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.tariff_percent)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.write_off_period),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = state.days,
                        onValueChange = onDaysChange,
                        modifier = Modifier.weight(1f).padding(end = 4.dp),
                        label = { Text(stringResource(R.string.days)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    )
                    OutlinedTextField(
                        value = state.hours,
                        onValueChange = onHoursChange,
                        modifier = Modifier.weight(1f).padding(horizontal = 4.dp),
                        label = { Text(stringResource(R.string.hours)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    )
                    OutlinedTextField(
                        value = state.minutes,
                        onValueChange = onMinutesChange,
                        modifier = Modifier.weight(1f).padding(start = 4.dp),
                        label = { Text(stringResource(R.string.minutes)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    )
                }
                state.error?.let { message ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onCreateRate,
                    enabled = !state.isLoading &&
                        state.name.isNotBlank() &&
                        state.percent.toIntOrNull() != null &&
                        state.percent.toIntOrNull() in 0..100,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = if (state.isLoading) stringResource(R.string.creating) else stringResource(R.string.create_rate),
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }
        }
    }
}
