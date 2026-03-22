package com.example.g_bankforemployees.feature.authorization.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.g_bankforemployees.R
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.g_bankforemployees.common.presentation.component.ErrorState
import com.example.g_bankforemployees.common.presentation.component.LoadingState

@Composable
fun SsoLoginScreen(viewModel: SsoLoginViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    when (val currentState = state) {
        SsoLoginScreenState.Loading -> LoadingState()
        is SsoLoginScreenState.Error -> ErrorState(
            title = stringResource(R.string.error),
            description = currentState.message,
            onRetry = viewModel::onRetry,
        )
        SsoLoginScreenState.Default -> Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Button(
                onClick = viewModel::onLoginClick,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onSurface,
                    contentColor = MaterialTheme.colorScheme.surface,
                ),
            ) {
                Text(text = stringResource(R.string.login_button))
            }
        }
    }
}

