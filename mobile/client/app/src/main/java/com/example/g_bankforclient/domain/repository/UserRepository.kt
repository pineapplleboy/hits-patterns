package com.example.g_bankforclient.domain.repository

import com.example.g_bankforclient.domain.models.User

interface UserRepository {
    suspend fun getMyProfile(): User
}
