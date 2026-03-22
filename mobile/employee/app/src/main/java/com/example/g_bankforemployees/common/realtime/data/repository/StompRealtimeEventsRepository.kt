package com.example.g_bankforemployees.common.realtime.data.repository

import com.example.g_bankforemployees.common.realtime.data.model.BalanceRealtimeBodyDto
import com.example.g_bankforemployees.common.realtime.data.model.OperationRealtimeBodyDto
import com.example.g_bankforemployees.common.realtime.data.model.OperationStatusRealtimeBodyDto
import com.example.g_bankforemployees.common.realtime.domain.model.RealtimeEvent
import com.example.g_bankforemployees.common.realtime.domain.model.RealtimeOperation
import com.example.g_bankforemployees.common.realtime.domain.repository.RealtimeEventsRepository
import com.example.g_bankforemployees.feature.authorization.domain.TokenStorage
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.concurrent.atomic.AtomicLong

private const val SOCKET_URL = "ws://91.227.18.176/core/ws"
private const val SOCKET_HOST = "91.227.18.176"

class StompRealtimeEventsRepository(
    private val okHttpClient: OkHttpClient,
    private val json: Json,
    private val tokenStorage: TokenStorage,
) : RealtimeEventsRepository {

    private val _events = MutableSharedFlow<RealtimeEvent>(extraBufferCapacity = 32)
    override val events: SharedFlow<RealtimeEvent> = _events

    private val socketIdGenerator = AtomicLong(0L)

    @Volatile
    private var webSocket: WebSocket? = null

    @Volatile
    private var subscribedUserId: String? = null

    @Volatile
    private var isConnected: Boolean = false

    @Volatile
    private var isConnecting: Boolean = false

    @Volatile
    private var currentSocketId: Long = 0L

    @Volatile
    private var disconnectRequested: Boolean = false

    @Synchronized
    override fun connect(userId: String) {
        if (subscribedUserId == userId && (isConnected || isConnecting)) {
            return
        }

        disconnectInternal()
        subscribedUserId = userId
        disconnectRequested = false
        isConnecting = true

        val token = tokenStorage.getToken()
        val requestBuilder = Request.Builder().url(SOCKET_URL)
        if (!token.isNullOrBlank()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        val socketId = socketIdGenerator.incrementAndGet()
        currentSocketId = socketId
        webSocket = okHttpClient.newWebSocket(
            requestBuilder.build(),
            createSocketListener(socketId),
        )
    }

    @Synchronized
    override fun disconnect() {
        disconnectInternal()
    }

    @Synchronized
    private fun disconnectInternal() {
        disconnectRequested = true
        isConnected = false
        isConnecting = false
        subscribedUserId = null
        webSocket?.cancel()
        webSocket = null
    }

    private fun createSocketListener(socketId: Long): WebSocketListener = object : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            if (isStaleSocket(socketId, webSocket)) return

            val token = tokenStorage.getToken()
            val connectHeaders = buildList {
                add("accept-version:1.2")
                add("host:$SOCKET_HOST")
                if (!token.isNullOrBlank()) {
                    add("Authorization:Bearer $token")
                    add("authorization:Bearer $token")
                }
            }
            webSocket.send(
                buildFrame(
                    command = "CONNECT",
                    headers = connectHeaders,
                ),
            )
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            if (isStaleSocket(socketId, webSocket)) return

            val normalizedFrame = normalizeFrame(text)
            when {
                normalizedFrame.startsWith("CONNECTED") -> {
                    isConnecting = false
                    isConnected = true
                    val userId = subscribedUserId ?: return
                    webSocket.send(
                        buildFrame(
                            command = "SUBSCRIBE",
                            headers = listOf(
                                "id:sub-0",
                                "destination:/topic/users/$userId",
                            ),
                        ),
                    )
                }

                normalizedFrame.startsWith("MESSAGE") -> {
                    extractBody(normalizedFrame)
                        ?.let(::parseEvent)
                        ?.let(_events::tryEmit)
                }

                normalizedFrame.startsWith("ERROR") -> {
                    isConnecting = false
                    isConnected = false
                }
            }
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            if (isStaleSocket(socketId, webSocket)) return

            isConnecting = false
            isConnected = false
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            if (isStaleSocket(socketId, webSocket) || disconnectRequested) return

            isConnecting = false
            isConnected = false
        }
    }

    private fun isStaleSocket(
        socketId: Long,
        socket: WebSocket,
    ): Boolean = socketId != currentSocketId || socket !== webSocket

    private fun normalizeFrame(frame: String): String =
        frame
            .replace("\r\n", "\n")
            .trimStart('\n', '\r')

    private fun buildFrame(
        command: String,
        headers: List<String>,
        body: String = "",
    ): String {
        val headerSection = headers.joinToString(separator = "\n")
        return if (body.isBlank()) {
            "$command\n$headerSection\n\n\u0000"
        } else {
            "$command\n$headerSection\n\n$body\u0000"
        }
    }

    private fun extractBody(frame: String): String? {
        val separatorIndex = frame.indexOf("\n\n")
        if (separatorIndex < 0) return null
        return frame.substring(separatorIndex + 2).trimEnd('\u0000')
    }

    private fun parseEvent(body: String): RealtimeEvent? {
        val payload = runCatching { json.parseToJsonElement(body).jsonObject }
            .getOrNull()
            ?: return null

        val messageType = payload["messageType"]
            ?.jsonPrimitive
            ?.content
            ?: return null

        val bodyElement = payload["body"] ?: return RealtimeEvent.Unknown(messageType)

        return runCatching {
            when (messageType) {
                "OPERATION_CREATE" -> {
                    val operation = json.decodeFromJsonElement<OperationRealtimeBodyDto>(bodyElement)
                    RealtimeEvent.OperationUpsert(
                        operation = RealtimeOperation(
                            operationId = operation.operationId,
                            accountNumberFrom = operation.accountNumberFrom,
                            userIdFrom = operation.userIdFrom,
                            recipientAccountNumber = operation.recipientAccountNumber,
                            amount = operation.amount,
                            transferAccountType = operation.transferAccountType,
                            actionType = operation.actionType,
                            status = operation.status,
                            createTime = operation.createTime,
                        ),
                    )
                }

                "OPERATION_STATUS_UPDATE" -> {
                    val statusUpdate = json.decodeFromJsonElement<OperationStatusRealtimeBodyDto>(bodyElement)
                    RealtimeEvent.OperationStatusChanged(
                        operationId = statusUpdate.operationId,
                        newStatus = statusUpdate.newStatus,
                    )
                }

                "BANK_ACCOUNT_SUM_UPDATE" -> {
                    val balanceUpdate = json.decodeFromJsonElement<BalanceRealtimeBodyDto>(bodyElement)
                    RealtimeEvent.BankAccountBalanceChanged(
                        balance = balanceUpdate.balance,
                        accountId = balanceUpdate.accountId,
                        accountNumber = balanceUpdate.accountNumber,
                    )
                }

                "CREDIT_ACCOUNT_DEPT_UPDATE" -> {
                    val balanceUpdate = json.decodeFromJsonElement<BalanceRealtimeBodyDto>(bodyElement)
                    RealtimeEvent.CreditAccountDebtChanged(
                        balance = balanceUpdate.balance,
                        accountId = balanceUpdate.accountId,
                        accountNumber = balanceUpdate.accountNumber,
                    )
                }

                else -> RealtimeEvent.Unknown(messageType)
            }
        }.getOrNull()
    }
}
