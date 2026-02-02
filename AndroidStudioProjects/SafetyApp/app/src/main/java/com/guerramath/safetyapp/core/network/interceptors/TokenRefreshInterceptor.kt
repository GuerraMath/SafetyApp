package com.guerramath.safetyapp.core.network.interceptors

import com.google.gson.Gson
import com.guerramath.safetyapp.auth.data.dto.AuthResponse
import com.guerramath.safetyapp.auth.data.dto.RefreshTokenRequest
import com.guerramath.safetyapp.auth.data.token.TokenManager
import com.guerramath.safetyapp.core.network.ApiException
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.net.HttpURLConnection

/**
 * Interceptor que automaticamente renova o token quando expira.
 */
class TokenRefreshInterceptor(
    private val tokenManager: TokenManager,
    // Se estiver usando BuildConfig.BASE_URL, pode remover esse parâmetro e usar direto
    private val baseUrl: String
) : Interceptor {

    private val gson = Gson()
    private val refreshLock = Any()

    @Volatile
    private var isRefreshing = false

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val response = chain.proceed(originalRequest)

        // 1. Se não for 401 Unauthorized, retorna normal
        if (response.code != HttpURLConnection.HTTP_UNAUTHORIZED) {
            return response
        }

        // 2. Se o erro 401 veio do próprio endpoint de refresh, aborta (loop infinito)
        if (originalRequest.url.encodedPath.contains("auth/refresh", ignoreCase = true) ||
            originalRequest.url.encodedPath.contains("auth/login", ignoreCase = true)) {
            return response
        }

        // Fecha o body da resposta de erro para liberar recursos
        response.close()

        synchronized(refreshLock) {
            // Pega o token atual salvo no dispositivo
            val currentToken = tokenManager.getToken()
            // Pega o token que foi enviado na requisição que falhou
            val originalToken = originalRequest.header("Authorization")?.removePrefix("Bearer ")

            // 3. Cenário: Outra thread já atualizou o token enquanto essa esperava no Lock
            if (currentToken != null && originalToken != null && currentToken != originalToken) {
                // Apenas refaz a requisição com o novo token
                val newRequest = originalRequest.newBuilder()
                    .header("Authorization", "Bearer $currentToken")
                    .build()
                return chain.proceed(newRequest)
            }

            // 4. Tenta renovar o token
            try {
                val newToken = refreshToken()

                if (newToken != null) {
                    // Sucesso! Refaz a requisição original com o novo token
                    val newRequest = originalRequest.newBuilder()
                        .header("Authorization", "Bearer $newToken")
                        .build()
                    return chain.proceed(newRequest)
                } else {
                    // Falha no refresh: força logout e lança erro
                    tokenManager.clearTokensSync()
                    throw ApiException.RefreshTokenInvalidException()
                }
            } catch (e: Exception) {
                // Qualquer erro no processo limpa a sessão
                tokenManager.clearTokensSync()
                throw ApiException.RefreshTokenInvalidException()
            }
        }
    }

    /**
     * Faz a chamada síncrona para a API de refresh.
     * Cria um OkHttpClient novo e "limpo" para evitar loops de interceptors.
     */
    private fun refreshToken(): String? {
        // Usa o novo método getRefreshToken() que criamos no Passo 3
        val refreshToken = tokenManager.getRefreshToken() ?: return null

        // Cria um cliente novo sem interceptors para evitar recursividade
        val client = OkHttpClient.Builder().build()

        val requestBody = gson.toJson(RefreshTokenRequest(refreshToken))
            .toRequestBody("application/json".toMediaType())

        // Atenção à barra final na baseUrl
        val endpoint = if (baseUrl.endsWith("/")) "auth/refresh" else "/auth/refresh"

        val request = Request.Builder()
            .url("$baseUrl$endpoint")
            .post(requestBody)
            .build()

        return try {
            val response = client.newCall(request).execute()

            if (response.isSuccessful && response.body != null) {
                val responseString = response.body!!.string()
                val authResponse = gson.fromJson(responseString, AuthResponse::class.java)

                // Salva os novos tokens
                runBlocking {
                    tokenManager.saveTokens(
                        authResponse.token,
                        authResponse.refreshToken
                    )
                }
                authResponse.token
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}