package com.example.g_bankforemployees.feature.users_list.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: String,
    val name: String? = null,
    val phone: String? = null,
    val ban: Boolean,
    @SerialName("userRole") val userRole: String,
    val author: String? = null,
    val isBannable: Boolean = false,
)
