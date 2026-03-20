package com.example.g_bankforclient.data.sso

import android.net.Uri
import net.openid.appauth.connectivity.ConnectionBuilder
import java.net.HttpURLConnection
import java.net.URL

/**
 * AppAuth by default allows only HTTPS endpoints.
 * Backend is currently exposed over HTTP, so we explicitly allow both HTTP and HTTPS.
 */
object AllowHttpConnectionBuilder : ConnectionBuilder {
    override fun openConnection(uri: Uri): HttpURLConnection {
        val scheme = uri.scheme?.lowercase()
        require(scheme == "http" || scheme == "https") {
            "Unsupported URI scheme for AppAuth: $scheme"
        }
        return URL(uri.toString()).openConnection() as HttpURLConnection
    }
}

