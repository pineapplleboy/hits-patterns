package com.example.g_bankforclient.data.realtime

import android.content.Context
import android.util.Log
import com.example.g_bankforclient.domain.TokenStorage
import com.example.g_bankforclient.domain.models.UserRealtimeEvent
import com.example.g_bankforclient.domain.repository.UserRealtimeRepository
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.dto.StompHeader
import ua.naiksoftware.stomp.dto.StompMessage
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRealtimeRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val tokenStorage: TokenStorage,
    private val gson: Gson,
) : UserRealtimeRepository {

    override fun observeUserEvents(userId: String): Flow<UserRealtimeEvent> = callbackFlow {
        val tag = "CoreStomp"
        val host = "91.227.18.176"
        val wsUrl = "ws://$host/core/ws"
        val destination = "/topic/users/$userId"

        val stompClient = Stomp.over(
            Stomp.ConnectionProvider.OKHTTP,
            wsUrl
        )

        val authToken = tokenStorage.getToken()
        val stompConnectHeaders = buildList {
            // Per your backend requirements: CONNECT accept-version + host.
            // (The library already sets accept-version; we avoid duplicating it.)
            add(StompHeader("host", host))

            // Spring STOMP websocket often requires JWT in CONNECT headers.
            if (!authToken.isNullOrBlank()) {
                add(StompHeader("Authorization", "Bearer $authToken"))
            }
        }

        stompClient.connect(stompConnectHeaders)

        val subscription: Disposable = stompClient
            .topic(destination)
            .subscribe(
                { msg: StompMessage ->
                    val payload = msg.payload.orEmpty()
                    val envelope =
                        runCatching { gson.fromJson(payload, CoreWsEnvelope::class.java) }
                            .getOrNull() ?: return@subscribe

                    val event = CoreWsMessageParsers.parseEnvelope(payload, envelope)
                    if (event != null) {
                        Log.d(tag, "WS recv: ${envelope.messageType}")
                        trySend(event)
                    } else {
                        Log.d(tag, "WS recv: unknown messageType=${envelope.messageType}")
                    }
                },
                { throwable ->
                    Log.e(tag, "WS error", throwable)
                    close(throwable)
                }
            )

        awaitClose {
            subscription.dispose()
            stompClient.disconnect()
        }
    }
}

