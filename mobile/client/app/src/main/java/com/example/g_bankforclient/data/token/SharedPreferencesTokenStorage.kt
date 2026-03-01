package com.example.g_bankforclient.data.token

import android.content.Context
import androidx.core.content.edit
import com.example.g_bankforclient.domain.TokenStorage

private const val PREFS_NAME = "auth_prefs"
private const val KEY_TOKEN = "auth_token"
private const val KEY_USER_ID = "user_id"

class SharedPreferencesTokenStorage(
    context: Context,
) : TokenStorage {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun getToken(): String? = prefs.getString(KEY_TOKEN, null)

    override fun setToken(token: String?) {
        prefs.edit { putString(KEY_TOKEN, token) }
    }

    override fun clearToken() {
        prefs.edit {
            remove(KEY_TOKEN)
                .remove(KEY_USER_ID)
        }
    }

    override fun getUserId(): String? = prefs.getString(KEY_USER_ID, null)

    override fun setUserId(userId: String?) {
        prefs.edit { putString(KEY_USER_ID, userId) }
    }
}
