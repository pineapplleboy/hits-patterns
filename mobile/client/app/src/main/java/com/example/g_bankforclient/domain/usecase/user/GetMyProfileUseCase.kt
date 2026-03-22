package com.example.g_bankforclient.domain.usecase.user

import com.example.g_bankforclient.domain.models.User
import com.example.g_bankforclient.domain.repository.UserRepository
import javax.inject.Inject

class GetMyProfileUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(): User = repository.getMyProfile()
}
