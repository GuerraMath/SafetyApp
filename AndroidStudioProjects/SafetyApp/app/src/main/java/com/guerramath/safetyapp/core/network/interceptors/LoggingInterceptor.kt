
package com.guerramath.safetyapp.core.network.interceptors

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.util.concurrent.TimeUnit

/**
 * Interceptor para logging de requisições em modo debug.
 * Mostra detalhes de request/response no Logcat.
 */
class LoggingInterceptor : Interceptor {

    companion object {
        private const val TAG = "HTTP"
        private const val MAX_BODY_LOG_LENGTH = 4000
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val startTime = System.nanoTime()

        // Log do Request
        Log.d(TAG, "┌────────────────────────────────────────────────────────")
        Log.d(TAG, "│ ➡️ ${request.method} ${request.url}")
        Log.d(TAG, "│ Headers:")
        request.headers.forEach { (name, value) ->
            // Oculta parte do token por segurança
            val displayValue = if (name.equals("Authorization", ignoreCase = true)) {
                value.take(20) + "..."
            } else {
                value
            }
            Log.d(TAG, "│   $name: $displayValue")
        }

        // Log do body do request (se houver)
        request.body?.let { body ->
            val buffer = okio.Buffer()
            body.writeTo(buffer)
            val bodyString = buffer.readUtf8()
            if (bodyString.isNotEmpty()) {
                Log.d(TAG, "│ Body: ${bodyString.take(MAX_BODY_LOG_LENGTH)}")
            }
        }

        // Executa a requisição
        val response: Response
        try {
            response = chain.proceed(request)
        } catch (e: Exception) {
            val duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime)
            Log.e(TAG, "│ ❌ FAILED after ${duration}ms: ${e.message}")
            Log.d(TAG, "└────────────────────────────────────────────────────────")
            throw e
        }

        val duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime)

        // Log do Response
        val statusEmoji = when {
            response.isSuccessful -> "✅"
            response.code in 400..499 -> "⚠️"
            else -> "❌"
        }

        Log.d(TAG, "│")
        Log.d(TAG, "│ $statusEmoji ${response.code} ${response.message} (${duration}ms)")

        // Log do body do response
        val responseBody = response.body
        val contentType = responseBody?.contentType()
        val bodyString = responseBody?.string() ?: ""

        if (bodyString.isNotEmpty()) {
            Log.d(TAG, "│ Response: ${bodyString.take(MAX_BODY_LOG_LENGTH)}")
            if (bodyString.length > MAX_BODY_LOG_LENGTH) {
                Log.d(TAG, "│ ... (truncated, total: ${bodyString.length} chars)")
            }
        }

        Log.d(TAG, "└────────────────────────────────────────────────────────")

        // Reconstrói o response body (já foi consumido)
        return response.newBuilder()
            .body(bodyString.toResponseBody(contentType))
            .build()
    }
}

