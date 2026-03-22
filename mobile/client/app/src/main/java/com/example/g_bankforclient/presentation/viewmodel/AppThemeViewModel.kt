package com.example.g_bankforclient.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.g_bankforclient.domain.usecase.settings.GetUserSettingsUseCase
import com.example.g_bankforclient.domain.usecase.settings.UpdateUserDarkModeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppThemeViewModel @Inject constructor(
    private val getUserSettingsUseCase: GetUserSettingsUseCase,
    private val updateUserDarkModeUseCase: UpdateUserDarkModeUseCase
) : ViewModel() {

    private val _isDarkTheme = MutableStateFlow<Boolean?>(null)
    val isDarkTheme: StateFlow<Boolean?> = _isDarkTheme.asStateFlow()

    init {
        loadTheme()
    }

    fun loadTheme() {
        viewModelScope.launch {
            runCatching { getUserSettingsUseCase() }
                .onSuccess { settings ->
                    _isDarkTheme.value = settings.isDarkMode
                }
                .onFailure { _ ->
                    _isDarkTheme.value = null
                }
        }
    }

    fun setDarkTheme(dark: Boolean) {
        _isDarkTheme.value = dark
        viewModelScope.launch {
            runCatching { updateUserDarkModeUseCase(dark) }
                .onSuccess { settings ->
                    _isDarkTheme.value = settings.isDarkMode
                }
        }
    }
}
