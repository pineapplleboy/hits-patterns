package com.example.g_bankforemployees.common.navigation

interface Navigator {

    fun navigateToUsersList()

    fun navigateToAuthorizationAndClearStack()

    fun navigateToClientDetails(userId: String, userName: String = "", userPhone: String = "")

    fun navigateBack()

    fun setOnReturnFromUserCreate(callback: (() -> Unit)?)

    fun setOnReturnFromCreditRateCreate(callback: (() -> Unit)?)

    fun navigateToAccountOperations(
        userId: String,
        accountNumber: String,
        transferType: String = "BANK_ACCOUNT",
        userName: String = "",
    )

    fun navigateToUserCreate()

    fun navigateToCreditRateCreate()

    fun navigateBackFromUserCreate()

    fun navigateBackFromCreditRateCreate()
}
