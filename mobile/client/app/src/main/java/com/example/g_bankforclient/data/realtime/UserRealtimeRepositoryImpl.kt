package com.example.g_bankforclient.data.realtime

import android.content.Context
import com.example.g_bankforclient.domain.TokenStorage
import com.example.g_bankforclient.domain.models.UserRealtimeEvent
import com.example.g_bankforclient.domain.repository.UserRealtimeRepository
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.shareIn
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.dto.LifecycleEvent
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

    private val scope = CoroutineScope(
        SupervisorJob() + Dispatchers.IO + CoroutineExceptionHandler { _, _ -> }
    )

    private var cachedUserId: String? = null
    private var cachedFlow: SharedFlow<UserRealtimeEvent>? = null

    @Synchronized
    override fun observeUserEvents(userId: String): Flow<UserRealtimeEvent> {
        if (cachedUserId == userId) {
            cachedFlow?.let { return it }
        }
        val flow = buildRawFlow(userId)
            .shareIn(
                scope = scope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000L),
                replay = 0
            )
        cachedUserId = userId
        cachedFlow = flow
        return flow
    }

    private fun buildRawFlow(userId: String): Flow<UserRealtimeEvent> = callbackFlow {
        val host = "91.227.18.176"
        val wsUrl = "ws://$host/core/ws"
        val destination = "/topic/users/$userId"

        val stompClient = Stomp.over(
            Stomp.ConnectionProvider.OKHTTP,
            wsUrl
        )

        val authToken = tokenStorage.getToken()
        val stompConnectHeaders = buildList {
            add(StompHeader("host", host))
            if (!authToken.isNullOrBlank()) {
                add(StompHeader("Authorization", "Bearer $authToken"))
            }
        }

        val lifecycleDisposable: Disposable = stompClient.lifecycle()
            .subscribe(
                { event ->
                    when (event.type) {
                        LifecycleEvent.Type.CLOSED -> close()
                        LifecycleEvent.Type.ERROR -> close()
                        else -> Unit
                    }
                },
                { close() }
            )

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
                    if (event != null) trySend(event)
                },
                { close() }
            )

        awaitClose {
            lifecycleDisposable.dispose()
            subscription.dispose()
            stompClient.disconnect()
        }
    }
}
