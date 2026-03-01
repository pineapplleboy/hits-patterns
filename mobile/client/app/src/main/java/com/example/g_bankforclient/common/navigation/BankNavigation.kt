package com.example.g_bankforclient.common.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.g_bankforclient.presentation.ui.screens.AccountDetailsScreen
import com.example.g_bankforclient.presentation.ui.screens.AuthorizationScreen
import com.example.g_bankforclient.presentation.ui.screens.CreateAccountScreen
import com.example.g_bankforclient.presentation.ui.screens.CreditDetailsScreen
import com.example.g_bankforclient.presentation.ui.screens.CreditsScreen
import com.example.g_bankforclient.presentation.ui.screens.HomeScreen
import com.example.g_bankforclient.presentation.ui.screens.TransactionHistoryScreen

@Composable
fun BankNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Authorization.route,
        modifier = modifier
    ) {
        composable(Screen.Authorization.route) {
            AuthorizationScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Authorization.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Home.route) {
            HomeScreen(
                onAccountClick = { accountId ->
                    navController.navigate(Screen.AccountDetails.createRoute(accountId))
                },
                onCreateAccount = {
                    navController.navigate(Screen.CreateAccount.route)
                },
                onLogout = {
                    navController.navigate(Screen.Authorization.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Credits.route) {
            CreditsScreen(
                onCreditClick = { creditId ->
                    navController.navigate(Screen.CreditDetails.createRoute(creditId))
                },
                onCreateCredit = { }
            )
        }

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

        composable(Screen.CreateAccount.route) {
            CreateAccountScreen(
                onBack = { navController.popBackStack() },
                onAccountCreated = {
                    navController.popBackStack()
                }
            )
        }

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
