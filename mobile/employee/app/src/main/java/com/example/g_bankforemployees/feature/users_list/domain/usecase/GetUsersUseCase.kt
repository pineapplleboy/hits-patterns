package com.example.g_bankforemployees.feature.users_list.domain.usecase

import com.example.g_bankforemployees.feature.users_list.domain.model.User
import com.example.g_bankforemployees.feature.users_list.domain.repository.UsersRepository

class GetUsersUseCase(
    private val usersRepository: UsersRepository,
) {

    suspend operator fun invoke(): Result<List<User>> = usersRepository.getUsers()
}
