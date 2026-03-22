package com.example.g_bankforclient.data.token

import android.content.Context
import com.example.g_bankforclient.domain.AuthStateStorage
import net.openid.appauth.AuthState

private const val AUTH_PREFS_NAME = "auth_prefs"
private const val KEY_AUTH_STATE_JSON = "auth_state_json"

class SharedPreferencesAuthStateStorage(
    context: Context,
) : AuthStateStorage {

    private val prefs = context.getSharedPreferences(AUTH_PREFS_NAME, Context.MODE_PRIVATE)

    override fun getAuthState(): AuthState? {
        val json = prefs.getString(KEY_AUTH_STATE_JSON, null) ?: return null
        return runCatching {
            AuthState.jsonDeserialize(json)
        }.getOrNull()
    }

    override fun setAuthState(authState: AuthState?) {
        prefs.edit().apply {
            if (authState == null) {
                remove(KEY_AUTH_STATE_JSON)
            } else {
                putString(KEY_AUTH_STATE_JSON, authState.jsonSerializeString())
            }
        }.apply()
    }

    override fun clearAuthState() {
        prefs.edit().remove(KEY_AUTH_STATE_JSON).apply()
    }
}

