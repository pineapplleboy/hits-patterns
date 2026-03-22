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
import com.example.g_bankforclient.presentation.ui.screens.MissedPaymentsScreen
import com.example.g_bankforclient.presentation.ui.screens.ProfileScreen
import com.example.g_bankforclient.presentation.ui.screens.TransactionHistoryScreen

@Composable
fun BankNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean = false,
    onThemeChange: (Boolean) -> Unit = {},
    onEnterMainApp: () -> Unit = {}
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
                onAccountClick = { accountId, isHidden ->
                    navController.navigate(Screen.AccountDetails.createRoute(accountId, isHidden))
                },
                onCreateAccount = {
                    navController.navigate(Screen.CreateAccount.route)
                },
                onLogout = {
                    navController.navigate(Screen.Authorization.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onEnterMainApp = onEnterMainApp
            )
        }

        composable(Screen.Credits.route) {
            CreditsScreen(
                onCreditClick = { creditId ->
                    navController.navigate(Screen.CreditDetails.createRoute(creditId))
                },
                onCreateCredit = { },
                onMissedPayments = {
                    navController.navigate(Screen.MissedPayments.route)
                }
            )
        }

        composable(Screen.MissedPayments.route) {
            MissedPaymentsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(isDarkTheme = isDarkTheme, onThemeChange = onThemeChange)
        }

        composable(
            route = Screen.AccountDetails.route,
            arguments = listOf(
                navArgument("accountId") { type = NavType.StringType },
                navArgument("isHidden") { type = NavType.BoolType; defaultValue = false }
            )
        ) { backStackEntry ->
            val accountId = backStackEntry.arguments?.getString("accountId") ?: return@composable
            val isHidden = backStackEntry.arguments?.getBoolean("isHidden") ?: false
            AccountDetailsScreen(
                accountId = accountId,
                isHidden = isHidden,
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
