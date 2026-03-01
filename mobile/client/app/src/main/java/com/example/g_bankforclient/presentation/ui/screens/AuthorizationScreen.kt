package com.example.g_bankforclient.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.g_bankforclient.R
import com.example.g_bankforclient.domain.models.UserCredentials
import com.example.g_bankforclient.presentation.state.AuthorizationScreenState
import com.example.g_bankforclient.presentation.viewmodel.AuthorizationViewModel
import com.example.g_bankforclient.ui.theme.GbankForClientTheme

@Composable
fun AuthorizationScreen(
    onLoginSuccess: () -> Unit,
) {
    val viewModel: AuthorizationViewModel = hiltViewModel()
    val screenState by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        when (val state = screenState) {
            is AuthorizationScreenState.Default -> DefaultAuthorizationScreen(
                credentials = state.credentials,
                name = state.name,
                isRegisterMode = state.isRegisterMode,
                onNameChange = viewModel::onNameChange,
                onLoginChange = viewModel::onLoginChange,
                onPasswordChange = viewModel::onPasswordChange,
                onLoginClick = viewModel::onLoginClick,
                onToggleMode = viewModel::toggleMode,
                modifier = Modifier.padding(padding)
            )

            AuthorizationScreenState.Loading -> LoadingAuthorizationScreen()

            AuthorizationScreenState.AuthSuccess -> {
                LaunchedEffect(Unit) {
                    onLoginSuccess()
                }
                LoadingAuthorizationScreen()
            }

            is AuthorizationScreenState.Error -> {
                LaunchedEffect(state.title, state.description) {
                    snackbarHostState.showSnackbar(
                        message = "${state.title}: ${state.description}"
                    )
                    viewModel.dismissError()
                }
                DefaultAuthorizationScreen(
                    credentials = state.credentials,
                    name = state.name,
                    isRegisterMode = state.isRegisterMode,
                    onNameChange = viewModel::onNameChange,
                    onLoginChange = viewModel::onLoginChange,
                    onPasswordChange = viewModel::onPasswordChange,
                    onLoginClick = viewModel::onLoginClick,
                    onToggleMode = viewModel::toggleMode,
                    modifier = Modifier.padding(padding)
                )
            }
        }
    }
}

@Composable
private fun DefaultAuthorizationScreen(
    credentials: UserCredentials,
    name: String,
    isRegisterMode: Boolean,
    onNameChange: (String) -> Unit,
    onLoginChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onToggleMode: () -> Unit,
    modifier: Modifier = Modifier,
) = Column(
    modifier = modifier
        .fillMaxSize()
        .imePadding()
        .verticalScroll(rememberScrollState())
        .padding(24.dp),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "OG-BANK",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (isRegisterMode) "Регистрация" else stringResource(R.string.login_for_employees),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.secondary
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (isRegisterMode) {
                OutlinedTextField(
                    value = name,
                    onValueChange = onNameChange,
                    label = { Text("Имя") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            OutlinedTextField(
                value = credentials.login,
                onValueChange = onLoginChange,
                label = { Text(if (isRegisterMode) "Телефон" else stringResource(R.string.login)) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = if (isRegisterMode) KeyboardType.Phone else KeyboardType.Text
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = credentials.password,
                onValueChange = onPasswordChange,
                label = { Text(stringResource(R.string.password)) },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                visualTransformation = if (passwordVisible)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                ),
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
                enabled = if (isRegisterMode) {
                    name.isNotBlank() && credentials.login.isNotBlank() && credentials.password.isNotBlank()
                } else {
                    credentials.login.isNotBlank() && credentials.password.isNotBlank()
                },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onSurface,
                    contentColor = MaterialTheme.colorScheme.surface,
                )
            ) {
                Text(
                    text = if (isRegisterMode) "Зарегистрироваться" else stringResource(R.string.login_button),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = onToggleMode,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (isRegisterMode) "Уже есть аккаунт? Войти" else "Нет аккаунта? Зарегистрироваться",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun LoadingAuthorizationScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.onSurface,
            strokeWidth = 4.dp,
            modifier = Modifier.size(48.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AuthorizationScreenPreview() {
    GbankForClientTheme {
        DefaultAuthorizationScreen(
            credentials = UserCredentials("", ""),
            name = "",
            isRegisterMode = false,
            onNameChange = {},
            onLoginChange = {},
            onPasswordChange = {},
            onLoginClick = {},
            onToggleMode = {}
        )
    }
}
