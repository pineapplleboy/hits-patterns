package com.example.g_bankforemployees.common.navigation

sealed class ScreenRoute(val route: String) {
    data object Main : ScreenRoute("home")
    data object Authorization: ScreenRoute("authorization")
}