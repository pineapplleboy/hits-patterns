package com.example.g_bankforclient.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.g_bankforclient.data.network.AuthEventBus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val authEventBus: AuthEventBus
) : ViewModel() {
    val isUnauthorized: StateFlow<Boolean> = authEventBus.isUnauthorized

    fun resetUnauthorized() {
        authEventBus.resetUnauthorized()
    }
}
