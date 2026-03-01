package com.example.g_bankforclient.data.network.model

import com.google.gson.annotations.SerializedName
import java.util.UUID

data class UserDTO(
    @SerializedName("id")
    val id: UUID,
    
    @SerializedName("name")
    val name: String?,
    
    @SerializedName("phone")
    val phone: String?,
    
    @SerializedName("ban")
    val ban: Boolean,
    
    @SerializedName("userRole")
    val userRole: UserRole,
    
    @SerializedName("author")
    val author: UUID?
)

enum class UserRole {
    @SerializedName("CLIENT")
    CLIENT,
    
    @SerializedName("EMPLOYEE")
    EMPLOYEE
}
