package com.example.g_bankforemployees.common.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.g_bankforemployees.feature.account_operations.presentation.AccountOperationsScreen
import com.example.g_bankforemployees.feature.authorization.presentation.SsoGateScreen
import com.example.g_bankforemployees.feature.authorization.presentation.SsoLoginScreen
import com.example.g_bankforemployees.feature.client_details.presentation.ClientDetailsScreen
import com.example.g_bankforemployees.feature.credit_history.presentation.CreditHistoryScreen
import com.example.g_bankforemployees.feature.credit_history.presentation.CreditHistoryViewModel
import com.example.g_bankforemployees.feature.credit_rate.presentation.CreditRateCreateScreen
import com.example.g_bankforemployees.feature.credit_rate.presentation.TariffsListScreen
import com.example.g_bankforemployees.feature.settings.presentation.SettingsScreen
import com.example.g_bankforemployees.feature.user_create.presentation.UserCreateScreen
import com.example.g_bankforemployees.feature.users_list.presentation.UsersListScreen
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun AppNavGraph(navController: NavHostController) {
    val context = LocalContext.current
    val navigator = remember(navController) { AppNavigator(navController) }
    NavigatorHolder.navigator = navigator

    NavHost(
        navController = navController,
        startDestination = ScreenRoute.SsoLogin.route,
    ) {
        composable(ScreenRoute.SsoLogin.route) {
            SsoLoginScreen(viewModel = koinViewModel())
        }
        composable(ScreenRoute.SsoGate.route) {
            SsoGateScreen(viewModel = koinViewModel(parameters = { parametersOf(context) }))
        }
        composable(ScreenRoute.UsersList.route) {
            UsersListScreen(viewModel = koinViewModel())
        }
        composable(ScreenRoute.ClientDetails.route) { backStackEntry ->
            ClientDetailsScreen(
                viewModel = koinViewModel(
                    viewModelStoreOwner = backStackEntry,
                    parameters = { parametersOf(backStackEntry.savedStateHandle) },
                ),
            )
        }
        composable(ScreenRoute.AccountOperations.route) { backStackEntry ->
            AccountOperationsScreen(
                viewModel = koinViewModel(
                    viewModelStoreOwner = backStackEntry,
                    parameters = { parametersOf(backStackEntry.savedStateHandle) },
                ),
            )
        }
        composable(ScreenRoute.CreditHistory.route) { backStackEntry ->
            CreditHistoryScreen(
                viewModel = koinViewModel<CreditHistoryViewModel>(
                    viewModelStoreOwner = backStackEntry,
                    parameters = { parametersOf(backStackEntry.savedStateHandle) },
                ),
            )
        }
        composable(ScreenRoute.CreditRateCreate.route) {
            CreditRateCreateScreen(viewModel = koinViewModel())
        }
        composable(ScreenRoute.TariffsList.route) {
            TariffsListScreen(viewModel = koinViewModel())
        }
        composable(ScreenRoute.Settings.route) {
            SettingsScreen(viewModel = koinViewModel())
        }
        composable(ScreenRoute.UserCreate.route) {
            UserCreateScreen(viewModel = koinViewModel())
        }
    }
}
