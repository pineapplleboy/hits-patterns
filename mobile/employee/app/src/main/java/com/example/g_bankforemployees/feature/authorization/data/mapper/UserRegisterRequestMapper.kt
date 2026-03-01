package com.example.g_bankforemployees.feature.authorization.data.mapper

import com.example.g_bankforemployees.feature.authorization.data.model.EmployeeRegisterDto
import com.example.g_bankforemployees.feature.authorization.data.model.RegisterDto
import com.example.g_bankforemployees.feature.authorization.domain.model.EmployeeRegistrationInput
import com.example.g_bankforemployees.feature.authorization.domain.model.RegisterUserInput

fun EmployeeRegistrationInput.toRegisterDto(): RegisterDto = RegisterDto(
    name = name,
    phone = phone,
    password = password,
)

fun RegisterUserInput.toRegisterUserDto(): EmployeeRegisterDto = EmployeeRegisterDto(
    name = name,
    phone = phone,
    password = password,
    userRole = userRole,
)
