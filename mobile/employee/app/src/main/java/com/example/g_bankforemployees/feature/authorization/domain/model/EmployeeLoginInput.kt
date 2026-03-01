package com.example.g_bankforemployees.feature.authorization.domain.model

data class EmployeeLoginInput(
    val phone: String,
    val password: String,
) {
    companion object {
        val Empty = EmployeeLoginInput(phone = "", password = "")
    }
}
