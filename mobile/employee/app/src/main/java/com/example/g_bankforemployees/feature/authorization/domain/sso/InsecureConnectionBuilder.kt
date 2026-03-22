package com.example.g_bankforemployees.feature.authorization.domain.sso

import android.net.Uri
import net.openid.appauth.connectivity.ConnectionBuilder
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class InsecureConnectionBuilder : ConnectionBuilder {

    override fun openConnection(uri: Uri): HttpURLConnection {
        try {
            val url = URL(uri.toString())
            return (url.openConnection() as HttpURLConnection).apply {
                instanceFollowRedirects = true
            }
        } catch (e: IOException) {
            throw e
        } catch (e: Exception) {
            throw IOException("Failed to open connection: ${e.message}", e)
        }
    }
}
