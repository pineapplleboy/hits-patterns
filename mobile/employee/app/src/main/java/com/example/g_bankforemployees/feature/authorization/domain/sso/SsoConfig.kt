package com.example.g_bankforemployees.feature.authorization.domain.sso

object SsoConfig {
    const val CLIENT_ID = "android_employeeee_app"
    const val AUTHORIZATION_URL = "http://91.227.18.176/identity/connect/authorize"
    const val TOKEN_URL = "http://91.227.18.176/identity/connect/token"
    const val END_SESSION_URL = "http://91.227.18.176/identity/connect/endsession"
    const val REDIRECT_URI = "com.employee.android:/callback"
    const val POST_LOGOUT_REDIRECT_URI = "com.employee.android:/logout"
}

