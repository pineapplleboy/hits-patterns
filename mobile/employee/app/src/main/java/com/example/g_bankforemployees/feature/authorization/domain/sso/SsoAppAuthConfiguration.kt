package com.example.g_bankforemployees.feature.authorization.domain.sso

import net.openid.appauth.AppAuthConfiguration

object SsoAppAuthConfiguration {
    fun build(): AppAuthConfiguration {
        return AppAuthConfiguration.Builder()
            .setConnectionBuilder(InsecureConnectionBuilder())
            .setSkipIssuerHttpsCheck(true)
            .build()
    }
}
