package com.example.g_bankforemployees.common.navigation

sealed class ScreenRoute(val route: String) {
    data object Authorization : ScreenRoute("authorization")
    data object UsersList : ScreenRoute("users_list")

    data object ClientDetails : ScreenRoute("client_details/{userId}/{userName}/{userPhone}") {
        fun buildRoute(userId: String, userName: String = "", userPhone: String = ""): String =
            "client_details/$userId/${java.net.URLEncoder.encode(userName, "UTF-8")}/${java.net.URLEncoder.encode(userPhone, "UTF-8")}"
    }

    data object AccountOperations : ScreenRoute("account_operations/{userId}/{accountNumber}/{transferType}/{userName}") {
        fun buildRoute(userId: String, accountNumber: String, transferType: String = "BANK_ACCOUNT", userName: String = ""): String =
            "account_operations/$userId/$accountNumber/$transferType/${java.net.URLEncoder.encode(userName, "UTF-8")}"
    }

    data object CreditRateCreate : ScreenRoute("credit_rate_create")

    data object UserCreate : ScreenRoute("user_create")
}