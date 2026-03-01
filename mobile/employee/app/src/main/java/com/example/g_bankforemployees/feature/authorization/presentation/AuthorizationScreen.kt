package com.example.g_bankforemployees.feature.authorization.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.g_bankforemployees.R
import com.example.g_bankforemployees.common.presentation.component.ErrorState
import com.example.g_bankforemployees.common.presentation.component.LoadingState
import com.example.g_bankforemployees.common.presentation.theme.BankTheme
import com.example.g_bankforemployees.common.presentation.theme.MediumGray
import com.example.g_bankforemployees.common.presentation.theme.Outline
import com.example.g_bankforemployees.feature.authorization.domain.model.EmployeeLoginInput
import com.example.g_bankforemployees.feature.authorization.domain.model.EmployeeRegistrationInput
import org.koin.androidx.compose.koinViewModel

@Composable
fun AuthorizationScreen(viewModel: AuthorizationScreenViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    AuthorizationScreenContent(
        state = state,
        selectedTabIndex = selectedTabIndex,
        onSelectedTabIndexChange = { selectedTabIndex = it },
        onLoginClick = viewModel::onLoginClick,
        onRegisterClick = viewModel::onRegisterClick,
        onLoginPhoneChange = viewModel::onLoginPhoneChange,
        onLoginPasswordChange = viewModel::onLoginPasswordChange,
        onRegistrationNameChange = viewModel::onRegistrationNameChange,
        onRegistrationPhoneChange = viewModel::onRegistrationPhoneChange,
        onRegistrationPasswordChange = viewModel::onRegistrationPasswordChange,
        onRetry = viewModel::onRetry,
    )
}

@Composable
private fun AuthorizationScreenContent(
    state: AuthorizationScreenState,
    selectedTabIndex: Int,
    onSelectedTabIndexChange: (Int) -> Unit,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onLoginPhoneChange: (String) -> Unit,
    onLoginPasswordChange: (String) -> Unit,
    onRegistrationNameChange: (String) -> Unit,
    onRegistrationPhoneChange: (String) -> Unit,
    onRegistrationPasswordChange: (String) -> Unit,
    onRetry: () -> Unit,
) {
    when (state) {
        is AuthorizationScreenState.Default -> {
            Scaffold(
                containerColor = MaterialTheme.colorScheme.background
            ) { padding ->
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .imePadding()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        elevation = CardDefaults.cardElevation(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = stringResource(R.string.brand_name),
                                style = MaterialTheme.typography.displayLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
                            )
                            TabRow(
                                selectedTabIndex = selectedTabIndex,
                                modifier = Modifier.fillMaxWidth(),
                                containerColor = MaterialTheme.colorScheme.surface,
                                contentColor = MaterialTheme.colorScheme.onSurface,
                            ) {
                                Tab(
                                    selected = selectedTabIndex == 0,
                                    onClick = { onSelectedTabIndexChange(0) },
                                    text = { Text(stringResource(R.string.login_button)) }
                                )
                                Tab(
                                    selected = selectedTabIndex == 1,
                                    onClick = { onSelectedTabIndexChange(1) },
                                    text = { Text(stringResource(R.string.registration)) }
                                )
                            }
                            when (selectedTabIndex) {
                                0 -> LoginTabContent(
                                    loginInput = state.loginInput,
                                    onLoginClick = onLoginClick,
                                    onPhoneChange = onLoginPhoneChange,
                                    onPasswordChange = onLoginPasswordChange,
                                )
                                1 -> RegisterTabContent(
                                    registrationInput = state.registrationInput,
                                    onRegisterClick = onRegisterClick,
                                    onNameChange = onRegistrationNameChange,
                                    onPhoneChange = onRegistrationPhoneChange,
                                    onPasswordChange = onRegistrationPasswordChange,
                                )
                            }
                        }
                    }
                }
            }
        }
        is AuthorizationScreenState.Error -> ErrorState(
            title = state.title,
            description = state.description,
            onRetry = onRetry,
        )
        AuthorizationScreenState.Loading -> LoadingState()
        else -> {}
    }
}

@Composable
private fun LoginTabContent(
    loginInput: EmployeeLoginInput,
    onLoginClick: () -> Unit,
    onPhoneChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        OutlinedTextField(
            value = loginInput.phone,
            onValueChange = onPhoneChange,
            label = { Text(stringResource(R.string.login)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = loginInput.password,
            onValueChange = onPasswordChange,
            label = { Text(stringResource(R.string.password)) },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            visualTransformation = if (passwordVisible)
                VisualTransformation.None
            else
                PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                TextButton(onClick = { passwordVisible = !passwordVisible }) {
                    Text(
                        if (passwordVisible)
                            stringResource(R.string.hide_password)
                        else
                            stringResource(R.string.show_password)
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onLoginClick,
            enabled = loginInput.phone.isNotBlank() && loginInput.password.isNotBlank(),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth().height(52.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onSurface,
                contentColor = MaterialTheme.colorScheme.surface,
                disabledContainerColor = Outline,
                disabledContentColor = MediumGray
            )
        ) {
            Text(
                text = stringResource(R.string.login_button),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
private fun RegisterTabContent(
    registrationInput: EmployeeRegistrationInput,
    onRegisterClick: () -> Unit,
    onNameChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        OutlinedTextField(
            value = registrationInput.phone,
            onValueChange = onPhoneChange,
            label = { Text(stringResource(R.string.phone)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = registrationInput.name,
            onValueChange = onNameChange,
            label = { Text(stringResource(R.string.name)) },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = registrationInput.password,
            onValueChange = onPasswordChange,
            label = { Text(stringResource(R.string.password)) },
            singleLine = true,
            visualTransformation = if (passwordVisible)
                VisualTransformation.None
            else
                PasswordVisualTransformation(),
            trailingIcon = {
                TextButton(onClick = { passwordVisible = !passwordVisible }) {
                    Text(
                        if (passwordVisible)
                            stringResource(R.string.hide_password)
                        else
                            stringResource(R.string.show_password)
                    )
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onRegisterClick,
            enabled = registrationInput.phone.isNotBlank() &&
                    registrationInput.name.isNotBlank() &&
                    registrationInput.password.isNotBlank(),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) {
            Text(stringResource(R.string.registration))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AuthorizationScreenPreview() {
    BankTheme {
        AuthorizationScreen(viewModel = koinViewModel())
    }
}
