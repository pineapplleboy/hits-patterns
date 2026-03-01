package com.example.g_bankforemployees.feature.user_create.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.ExperimentalMaterial3Api
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserCreateScreen(viewModel: UserCreateScreenViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    UserCreateScreenContent(
        state = state,
        onBackClick = viewModel::onBackClick,
        onNameChange = viewModel::onNameChange,
        onPhoneChange = viewModel::onPhoneChange,
        onPasswordChange = viewModel::onPasswordChange,
        onRoleIndexChange = viewModel::onRoleIndexChange,
        onCreateUser = viewModel::createUser,
    )
}

@Composable
private fun UserCreateScreenContent(
    state: UserCreateScreenState,
    onBackClick: () -> Unit,
    onNameChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRoleIndexChange: (Int) -> Unit,
    onCreateUser: () -> Unit,
) {
    Scaffold(
        topBar = {
            BankTopBar(
                title = stringResource(R.string.new_user),
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
                    text = stringResource(R.string.new_user),
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                TabRow(
                    selectedTabIndex = state.roleIndex,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                ) {
                    Tab(
                        selected = state.roleIndex == 0,
                        onClick = { onRoleIndexChange(0) },
                        text = { Text(stringResource(R.string.role_client)) },
                    )
                    Tab(
                        selected = state.roleIndex == 1,
                        onClick = { onRoleIndexChange(1) },
                        text = { Text(stringResource(R.string.role_employee)) },
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = state.name,
                    onValueChange = onNameChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.name)) },
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = state.phone,
                    onValueChange = onPhoneChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.phone)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = state.password,
                    onValueChange = onPasswordChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.password)) },
                )

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
                    onClick = onCreateUser,
                    enabled = !state.isLoading &&
                        state.name.isNotBlank() &&
                        state.phone.isNotBlank() &&
                        state.password.isNotBlank(),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = if (state.isLoading) stringResource(R.string.creating) else stringResource(R.string.create),
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }
        }
    }
}
