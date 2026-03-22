package com.example.g_bankforemployees.common.navigation

interface Navigator {

    fun navigateToUsersList()

    fun navigateToSsoLoginAndClearStack()

    fun navigateToSsoGate()

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

    fun navigateToCreditHistory(
        userId: String,
        userName: String = "",
    )

    fun navigateToUserCreate()

    fun navigateToCreditRateCreate()

    fun navigateToTariffsList()

    fun navigateToSettings()

    fun navigateBackFromUserCreate()

    fun navigateBackFromCreditRateCreate()
}
