package com.example.g_bankforemployees.feature.authorization.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.g_bankforemployees.R
import com.example.g_bankforemployees.common.presentation.component.ErrorState
import com.example.g_bankforemployees.common.presentation.component.LoadingState

@Composable
fun SsoGateScreen(viewModel: SsoGateViewModel) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val state by viewModel.state.collectAsStateWithLifecycle()

    DisposableEffect(lifecycleOwner, viewModel) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> viewModel.onScreenResumed()
                Lifecycle.Event.ON_STOP -> viewModel.onScreenStopped()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val currentState = state
    when (currentState) {
        SsoGateScreenState.Default -> LoadingState()
        SsoGateScreenState.Loading -> LoadingState()
        is SsoGateScreenState.Error -> ErrorState(
            title = stringResource(R.string.error),
            description = currentState.message,
            onRetry = viewModel::onRetry,
        )
    }
}
