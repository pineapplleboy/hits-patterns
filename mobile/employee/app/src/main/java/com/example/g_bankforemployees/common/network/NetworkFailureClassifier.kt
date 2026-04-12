package com.example.g_bankforemployees.common.network

import java.io.InterruptedIOException
import java.io.IOException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class NetworkFailureClassifier(
    private val config: RetryConfig,
) {

    fun isRetryableStatusCode(code: Int): Boolean =
        code in config.retryableHttpCodes || code in config.retryableHttpCodeRange

    fun isRetryableException(exception: IOException): Boolean =
        exception is SocketTimeoutException ||
            exception is UnknownHostException ||
            exception is SocketException ||
            exception is InterruptedIOException
}
