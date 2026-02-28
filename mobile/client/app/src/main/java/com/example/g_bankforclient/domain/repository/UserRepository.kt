package com.example.g_bankforclient.domain.repository

import com.example.g_bankforclient.common.models.User
import java.util.UUID

interface UserRepository {
    suspend fun getMyProfile(): User
}
