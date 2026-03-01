package com.example.g_bankforclient.data.network.model

import com.google.gson.annotations.SerializedName

data class UserLoginDTO(
    @SerializedName("phone")
    val phone: String?,
    @SerializedName("password")
    val password: String?
)

data class RegisterDTO(
    @SerializedName("name")
    val name: String?,
    @SerializedName("phone")
    val phone: String?,
    @SerializedName("password")
    val password: String?
)
