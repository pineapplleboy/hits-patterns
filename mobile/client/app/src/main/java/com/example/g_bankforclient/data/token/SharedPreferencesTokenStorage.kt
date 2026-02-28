package com.example.g_bankforclient.data.token

import android.content.Context
import com.example.g_bankforclient.domain.TokenStorage

private const val PREFS_NAME = "auth_prefs"
private const val KEY_TOKEN = "auth_token"

class SharedPreferencesTokenStorage(
    context: Context,
) : TokenStorage {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun getToken(): String? = prefs.getString(KEY_TOKEN, null)

    override fun setToken(token: String?) {
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }

    override fun clearToken() {
        prefs.edit().remove(KEY_TOKEN).apply()
    }
}
