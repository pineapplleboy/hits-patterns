package com.example.g_bankforemployees.feature.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.g_bankforemployees.common.navigation.NavigatorHolder
import com.example.g_bankforemployees.common.presentation.theme.ThemeStorage
import com.example.g_bankforemployees.feature.settings.domain.usecase.UpdateThemeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val themeStorage: ThemeStorage,
    private val updateThemeUseCase: UpdateThemeUseCase,
    private val navigatorHolder: NavigatorHolder,
) : ViewModel() {

    private val _state: MutableStateFlow<SettingsScreenState> =
        MutableStateFlow(SettingsScreenState.Loading)

    val state: StateFlow<SettingsScreenState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            themeStorage.isDarkTheme.collect { isDark ->
                _state.value = SettingsScreenState.Default(isDarkTheme = isDark)
            }
        }
    }

    fun onDarkThemeToggle(isDark: Boolean) {
        val previousTheme = themeStorage.isDarkTheme.value
        themeStorage.setDarkTheme(isDark)

        viewModelScope.launch {
            updateThemeUseCase(isDark)
                .onFailure {
                    themeStorage.setDarkTheme(previousTheme)
                }
        }
    }

    fun onBackClick() {
        navigatorHolder.navigator?.navigateBack()
    }
}
