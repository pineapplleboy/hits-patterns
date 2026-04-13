package com.example.g_bankforclient

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import android.os.Build
import com.example.g_bankforclient.common.navigation.BankNavigation
import com.example.g_bankforclient.common.navigation.Screen
import com.example.g_bankforclient.presentation.ui.components.BankBottomNavigation
import com.example.g_bankforclient.presentation.viewmodel.AppThemeViewModel
import com.example.g_bankforclient.presentation.viewmodel.SessionViewModel
import com.example.g_bankforclient.ui.theme.GbankForClientTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestNotificationPermissionIfNeeded()
        enableEdgeToEdge()
        setContent {
            BankRoot()
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return
        }

        val permissionGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED

        if (!permissionGranted) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}

@Composable
fun BankRoot() {
    val appThemeViewModel: AppThemeViewModel = hiltViewModel()
    val isDarkTheme by appThemeViewModel.isDarkTheme.collectAsStateWithLifecycle()
    val darkTheme = isDarkTheme ?: isSystemInDarkTheme()

    GbankForClientTheme(darkTheme = darkTheme) {
        BankApp(
            isDarkTheme = darkTheme,
            onThemeChange = { appThemeViewModel.setDarkTheme(it) },
            onEnterMainApp = { appThemeViewModel.loadTheme() }
        )
    }
}

@Composable
fun BankApp(
    isDarkTheme: Boolean = false,
    onThemeChange: (Boolean) -> Unit = {},
    onEnterMainApp: () -> Unit = {}
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val sessionViewModel: SessionViewModel = hiltViewModel()
    val isUnauthorized by sessionViewModel.isUnauthorized.collectAsStateWithLifecycle()
    LaunchedEffect(isUnauthorized) {
        if (isUnauthorized) {
            sessionViewModel.resetUnauthorized()
            navController.navigate(Screen.Authorization.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    val showBottomNav = currentRoute == Screen.Home.route ||
            currentRoute == Screen.Credits.route ||
            currentRoute == Screen.Profile.route

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
            modifier = Modifier
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues),
            isDarkTheme = isDarkTheme,
            onThemeChange = onThemeChange,
            onEnterMainApp = onEnterMainApp
        )
    }
}
