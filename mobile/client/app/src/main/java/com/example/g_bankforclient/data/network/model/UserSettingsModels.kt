package com.example.g_bankforclient.data.network.model

import com.google.gson.annotations.SerializedName

data class UserSettingsDTO(
    @SerializedName("isDarkMode")
    val isDarkMode: Boolean
)
