package com.example.g_bankforclient

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.g_bankforclient.common.navigation.BankNavigation
import com.example.g_bankforclient.common.navigation.Screen
import com.example.g_bankforclient.presentation.ui.components.BankBottomNavigation
import com.example.g_bankforclient.ui.theme.GbankForClientTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GbankForClientTheme {
                BankApp()
            }
        }
    }
}

@Composable
fun BankApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomNav = currentRoute == Screen.Home.route || currentRoute == Screen.Credits.route

    Scaffold(
        bottomBar = {
            if (showBottomNav) {
                BankBottomNavigation(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(Screen.Home.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        BankNavigation(
            navController = navController,
            modifier = Modifier.padding(paddingValues)
        )
    }
}
