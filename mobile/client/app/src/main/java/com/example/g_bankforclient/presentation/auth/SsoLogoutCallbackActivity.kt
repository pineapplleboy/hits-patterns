package com.example.g_bankforclient.presentation.auth

import android.os.Bundle
import androidx.activity.ComponentActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SsoLogoutCallbackActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Repository already clears local auth state; we don't need any extra UI work here.
        finish()
    }
}

