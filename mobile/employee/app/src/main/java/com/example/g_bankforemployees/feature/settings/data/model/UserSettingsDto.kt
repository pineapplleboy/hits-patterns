package com.example.g_bankforemployees.feature.settings.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UserSettingsDto(
    val isDarkMode: Boolean,
)
