package com.example.g_bankforemployees.feature.notifications.data.model

import kotlinx.serialization.Serializable

@Serializable
data class NotificationTokenRequestDto(
    val token: String,
)
