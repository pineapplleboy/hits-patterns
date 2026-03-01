package com.example.g_bankforemployees.common.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.g_bankforemployees.feature.account_operations.presentation.AccountOperationsScreen
import com.example.g_bankforemployees.feature.account_operations.presentation.AccountOperationsViewModel
import com.example.g_bankforemployees.feature.authorization.presentation.AuthorizationScreen
import com.example.g_bankforemployees.feature.authorization.presentation.AuthorizationScreenViewModel
import com.example.g_bankforemployees.feature.client_details.presentation.ClientDetailsScreen
import com.example.g_bankforemployees.feature.client_details.presentation.ClientDetailsViewModel
import com.example.g_bankforemployees.feature.credit_rate.presentation.CreditRateCreateScreen
import com.example.g_bankforemployees.feature.credit_rate.presentation.CreditRateCreateScreenViewModel
import com.example.g_bankforemployees.feature.user_create.presentation.UserCreateScreen
import com.example.g_bankforemployees.feature.user_create.presentation.UserCreateScreenViewModel
import com.example.g_bankforemployees.feature.users_list.presentation.UsersListScreen
import com.example.g_bankforemployees.feature.users_list.presentation.UsersListScreenViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun AppNavGraph(navController: NavHostController) {
    LaunchedEffect(navController) {
        NavigatorHolder.navigator = AppNavigator(navController)
    }

    NavHost(
        navController = navController,
        startDestination = ScreenRoute.UsersList.route,
    ) {
        composable(ScreenRoute.Authorization.route) {
            AuthorizationScreen(viewModel = koinViewModel())
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
        composable(ScreenRoute.CreditRateCreate.route) {
            CreditRateCreateScreen(viewModel = koinViewModel())
        }
        composable(ScreenRoute.UserCreate.route) {
            UserCreateScreen(viewModel = koinViewModel())
        }
    }
}
