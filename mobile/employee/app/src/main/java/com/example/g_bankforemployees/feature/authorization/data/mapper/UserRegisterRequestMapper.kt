package com.example.g_bankforemployees.feature.authorization.data.mapper

import com.example.g_bankforemployees.feature.authorization.data.model.EmployeeRegisterDto
import com.example.g_bankforemployees.feature.authorization.domain.model.EmployeeRegistrationInput
import com.example.g_bankforemployees.feature.authorization.domain.model.RegisterUserInput

private const val ROLE_EMPLOYEE = "EMPLOYEE"

fun EmployeeRegistrationInput.toEmployeeRegisterDto(): EmployeeRegisterDto = EmployeeRegisterDto(
    name = name,
    phone = phone,
    password = password,
    userRole = ROLE_EMPLOYEE,
)

fun RegisterUserInput.toRegisterDto(): EmployeeRegisterDto = EmployeeRegisterDto(
    name = name,
    phone = phone,
    password = password,
    userRole = userRole,
)
