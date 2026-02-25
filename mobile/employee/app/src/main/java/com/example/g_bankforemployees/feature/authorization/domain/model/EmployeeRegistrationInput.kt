package com.example.g_bankforemployees.feature.authorization.domain.model

data class EmployeeRegistrationInput(
    val name: String,
    val phone: String,
    val password: String,
) {
    companion object {
        val Empty = EmployeeRegistrationInput(name = "", phone = "", password = "")
    }
}
