package com.example.g_bankforclient.common.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Authorization: Screen("authorization")
    object Credits : Screen("credits")
    object Profile : Screen("profile")
    object CreateAccount : Screen("create_account")

    object AccountDetails : Screen("account_details/{accountId}?isHidden={isHidden}") {
        fun createRoute(accountId: String, isHidden: Boolean = false) =
            "account_details/$accountId?isHidden=$isHidden"
    }

    object CreditDetails : Screen("credit_details/{creditId}") {
        fun createRoute(creditId: String) = "credit_details/$creditId"
    }

    object TransactionHistory : Screen("transaction_history/{accountId}") {
        fun createRoute(accountId: String) = "transaction_history/$accountId"
    }

    object MissedPayments : Screen("missed_payments")
}

sealed class BottomNavScreen(val route: String, val title: String) {
    object Home : BottomNavScreen("home", "Главная")
    object Credits : BottomNavScreen("credits", "Кредиты")
    object Profile : BottomNavScreen("profile", "Профиль")
}
