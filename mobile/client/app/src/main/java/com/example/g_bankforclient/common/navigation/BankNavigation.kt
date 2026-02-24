package com.example.g_bankforclient.common.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost

import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.g_bankforclient.common.navigation.*
import com.example.g_bankforclient.presentation.ui.screens.AccountDetailsScreen
import com.example.g_bankforclient.presentation.ui.screens.AuthorizationScreen
import com.example.g_bankforclient.presentation.ui.screens.CreateAccountScreen
import com.example.g_bankforclient.presentation.ui.screens.CreateCreditScreen
import com.example.g_bankforclient.presentation.ui.screens.CreditDetailsScreen
import com.example.g_bankforclient.presentation.ui.screens.CreditsScreen
import com.example.g_bankforclient.presentation.ui.screens.HomeScreen
import com.example.g_bankforclient.presentation.ui.screens.TransactionHistoryScreen
import com.example.g_bankforclient.presentation.viewmodel.BankViewModel

@Composable
fun BankNavigation(
    navController: NavHostController,
    viewModel: BankViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Authorization.route
    ) {
        // Авторизация
        composable(Screen.Authorization.route) {
            AuthorizationScreen(
                onLoginClick = {
                    navController.navigate(Screen.Home.route)
                }
            )
        }
        // Главный экран
        composable(Screen.Home.route) {
            HomeScreen(
                accounts = viewModel.accounts,
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
                credits = viewModel.credits,
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
            val accountId = backStackEntry.arguments?.getString("accountId")
            val account = viewModel.accounts.find { it.id == accountId }

            account?.let {
                AccountDetailsScreen(
                    account = it,
                    onBack = { navController.popBackStack() },
                    onDeposit = { amount -> viewModel.deposit(it.id, amount) },
                    onWithdrawal = { amount -> viewModel.withdrawal(it.id, amount) },
                    onViewHistory = {
                        navController.navigate(Screen.TransactionHistory.createRoute(it.id))
                    },
                    onCloseAccount = {
                        viewModel.closeAccount(it.id)
                        navController.popBackStack()
                    }
                )
            }
        }

        // Экран деталей кредита
        composable(
            route = Screen.CreditDetails.route,
            arguments = listOf(navArgument("creditId") { type = NavType.StringType })
        ) { backStackEntry ->
            val creditId = backStackEntry.arguments?.getString("creditId")
            val credit = viewModel.credits.find { it.id == creditId }

            credit?.let {
                CreditDetailsScreen(
                    credit = it,
                    accounts = viewModel.accounts,
                    onBack = { navController.popBackStack() },
                    onPayCredit = { accountId, amount ->
                        viewModel.payCredit(it.id, accountId, amount)
                    }
                )
            }
        }

        // Экран создания счета
        composable(Screen.CreateAccount.route) {
            CreateAccountScreen(
                onBack = { navController.popBackStack() },
                onCreateAccount = { name ->
                    viewModel.createAccount(name)
                    navController.popBackStack()
                }
            )
        }

        // Экран создания кредита
        composable(Screen.CreateCredit.route) {
            CreateCreditScreen(
                onBack = { navController.popBackStack() },
                onCreateCredit = { name, amount, rate ->
                    viewModel.createCredit(name, amount, rate)
                    navController.popBackStack()
                }
            )
        }

        // Экран истории операций
        composable(
            route = Screen.TransactionHistory.route,
            arguments = listOf(navArgument("accountId") { type = NavType.StringType })
        ) { backStackEntry ->
            val accountId = backStackEntry.arguments?.getString("accountId")
            val accountTransactions = viewModel.transactions.filter { it.accountId == accountId }

            TransactionHistoryScreen(
                transactions = accountTransactions,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
