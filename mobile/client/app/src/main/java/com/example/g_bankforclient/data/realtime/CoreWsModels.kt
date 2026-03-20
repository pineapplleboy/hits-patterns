package com.example.g_bankforclient.data.realtime

import com.google.gson.JsonObject

data class CoreWsEnvelope(
    val messageType: String,
    val body: JsonObject
)

