package com.example.g_bankforemployees.feature.settings.presentation

sealed interface SettingsScreenState {

    data object Loading : SettingsScreenState

    data class Default(
        val isDarkTheme: Boolean,
    ) : SettingsScreenState

    data class Error(
        val message: String,
    ) : SettingsScreenState
}

