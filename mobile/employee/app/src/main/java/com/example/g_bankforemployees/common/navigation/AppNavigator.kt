package com.example.g_bankforemployees.common.navigation

import androidx.navigation.NavController

class AppNavigator(
    private val navController: NavController,
) : Navigator {

    private var onReturnFromUserCreate: (() -> Unit)? = null
    private var onReturnFromCreditRateCreate: (() -> Unit)? = null

    override fun navigateToUsersList() {
        navController.navigate(ScreenRoute.UsersList.route) {
            popUpTo(ScreenRoute.SsoLogin.route) { inclusive = true }
        }
    }

    override fun navigateToSsoLoginAndClearStack() {
        navController.navigate(ScreenRoute.SsoLogin.route) {
            popUpTo(ScreenRoute.UsersList.route) { inclusive = true }
        }
    }

    override fun navigateToSsoGate() {
        navController.navigate(ScreenRoute.SsoGate.route) {
            popUpTo(ScreenRoute.SsoLogin.route) { inclusive = true }
        }
    }

    override fun navigateToClientDetails(userId: String, userName: String, userPhone: String) {
        navController.navigate(
            ScreenRoute.ClientDetails.buildRoute(userId, userName, userPhone),
        )
    }

    override fun navigateBack() {
        navController.popBackStack()
    }

    override fun setOnReturnFromUserCreate(callback: (() -> Unit)?) {
        onReturnFromUserCreate = callback
    }

    override fun setOnReturnFromCreditRateCreate(callback: (() -> Unit)?) {
        onReturnFromCreditRateCreate = callback
    }

    override fun navigateToAccountOperations(
        userId: String,
        accountNumber: String,
        transferType: String,
        userName: String,
    ) {
        navController.navigate(
            ScreenRoute.AccountOperations.buildRoute(userId, accountNumber, transferType, userName),
        )
    }

    override fun navigateToCreditHistory(userId: String, userName: String) {
        navController.navigate(
            ScreenRoute.CreditHistory.buildRoute(userId, userName),
        )
    }

    override fun navigateToUserCreate() {
        navController.navigate(ScreenRoute.UserCreate.route)
    }

    override fun navigateToCreditRateCreate() {
        navController.navigate(ScreenRoute.CreditRateCreate.route)
    }

    override fun navigateToTariffsList() {
        navController.navigate(ScreenRoute.TariffsList.route)
    }

    override fun navigateToSettings() {
        navController.navigate(ScreenRoute.Settings.route)
    }

    override fun navigateBackFromUserCreate() {
        onReturnFromUserCreate?.invoke()
        navigateBack()
    }

    override fun navigateBackFromCreditRateCreate() {
        onReturnFromCreditRateCreate?.invoke()
        navigateBack()
    }
}
