package com.example.g_bankforclient.common.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Authorization: Screen("authorization")
    object Credits : Screen("credits")
    object CreateAccount : Screen("create_account")

    object AccountDetails : Screen("account_details/{accountId}") {
        fun createRoute(accountId: String) = "account_details/$accountId"
    }

    object CreditDetails : Screen("credit_details/{creditId}") {
        fun createRoute(creditId: String) = "credit_details/$creditId"
    }

    object TransactionHistory : Screen("transaction_history/{accountId}") {
        fun createRoute(accountId: String) = "transaction_history/$accountId"
    }
}

sealed class BottomNavScreen(val route: String, val title: String) {
    object Home : BottomNavScreen("home", "Главная")
    object Credits : BottomNavScreen("credits", "Кредиты")
}
