package com.example.g_bankforemployees.feature.authorization.data.mapper

import com.example.g_bankforemployees.feature.authorization.data.model.EmployeeLoginDto
import com.example.g_bankforemployees.feature.authorization.domain.model.EmployeeLoginInput

fun EmployeeLoginInput.toEmployeeLoginDto(): EmployeeLoginDto = EmployeeLoginDto(
    phone = phone,
    password = password,
)
