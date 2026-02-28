package com.example.g_bankforclient.common.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost

import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.g_bankforclient.presentation.ui.screens.AccountDetailsScreen
import com.example.g_bankforclient.presentation.ui.screens.AuthorizationScreen
import com.example.g_bankforclient.presentation.ui.screens.CreateAccountScreen
import com.example.g_bankforclient.presentation.ui.screens.CreateCreditScreen
import com.example.g_bankforclient.presentation.ui.screens.CreditDetailsScreen
import com.example.g_bankforclient.presentation.ui.screens.CreditsScreen
import com.example.g_bankforclient.presentation.ui.screens.HomeScreen
import com.example.g_bankforclient.presentation.ui.screens.TransactionHistoryScreen

@Composable
fun BankNavigation(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Authorization.route
    ) {
        // Авторизация
        composable(Screen.Authorization.route) {
            AuthorizationScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Authorization.route) { inclusive = true }
                    }
                }
            )
        }
        // Главный экран
        composable(Screen.Home.route) {
            HomeScreen(
                onAccountClick = { accountId ->
                    navController.navigate(Screen.AccountDetails.createRoute(accountId))
                },
                onCreateAccount = {
                    navController.navigate(Screen.CreateAccount.route)
                }
            )
        }

        // Экран кредитов
        composable(Screen.Credits.route) {
            CreditsScreen(
                onCreditClick = { creditId ->
                    navController.navigate(Screen.CreditDetails.createRoute(creditId))
                },
                onCreateCredit = {
                    navController.navigate(Screen.CreateCredit.route)
                }
            )
        }

        // Экран деталей счета
        composable(
            route = Screen.AccountDetails.route,
            arguments = listOf(navArgument("accountId") { type = NavType.StringType })
        ) { backStackEntry ->
            val accountId = backStackEntry.arguments?.getString("accountId") ?: return@composable
            AccountDetailsScreen(
                accountId = accountId,
                onBack = { navController.popBackStack() },
                onViewHistory = {
                    navController.navigate(Screen.TransactionHistory.createRoute(accountId))
                },
                onAccountClosed = {
                    navController.popBackStack()
                }
            )
        }

        // Экран деталей кредита
        composable(
            route = Screen.CreditDetails.route,
            arguments = listOf(navArgument("creditId") { type = NavType.StringType })
        ) { backStackEntry ->
            val creditId = backStackEntry.arguments?.getString("creditId") ?: return@composable
            CreditDetailsScreen(
                creditId = creditId,
                onBack = { navController.popBackStack() }
            )
        }

        // Экран создания счета
        composable(Screen.CreateAccount.route) {
            CreateAccountScreen(
                onBack = { navController.popBackStack() },
                onAccountCreated = {
                    navController.popBackStack()
                }
            )
        }

        // Экран создания кредита
        composable(Screen.CreateCredit.route) {
            CreateCreditScreen(
                onBack = { navController.popBackStack() },
                onCreditCreated = {
                    navController.popBackStack()
                }
            )
        }

        // Экран истории операций
        composable(
            route = Screen.TransactionHistory.route,
            arguments = listOf(navArgument("accountId") { type = NavType.StringType })
        ) { backStackEntry ->
            val accountId = backStackEntry.arguments?.getString("accountId") ?: return@composable
            TransactionHistoryScreen(
                accountId = accountId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
