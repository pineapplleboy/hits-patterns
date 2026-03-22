package com.example.g_bankforemployees.common.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.example.g_bankforemployees.common.presentation.theme.BankTheme
import com.example.g_bankforemployees.common.presentation.theme.ThemeStorage
import com.example.g_bankforemployees.common.navigation.AppNavGraph
import org.koin.compose.koinInject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeStorage: ThemeStorage = koinInject()
            val isDarkTheme by themeStorage.isDarkTheme.collectAsStateWithLifecycle()
            BankTheme(isDarkTheme = isDarkTheme) {
                val navController = rememberNavController()
                AppNavGraph(navController)
            }
        }
    }
}
