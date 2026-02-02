package com.guerramath.safetyapp.core.network.interceptors

import com.guerramath.safetyapp.auth.data.token.TokenManager
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor que adiciona o token de autenticação a todas as requisições.
 * Adiciona automaticamente o cabeçalho "Authorization: Bearer <token>".
 */
class AuthInterceptor(
    private val tokenManager: TokenManager
) : Interceptor {

    companion object {
        // Endpoints que não requerem autenticação (lista pública)
        private val PUBLIC_ENDPOINTS = listOf(
            "/auth/login",
            "/auth/register",
            "/auth/forgot-password",
            "/auth/refresh"
        )
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestPath = originalRequest.url.encodedPath

        // 1. Se for endpoint público, passa direto sem token
        if (PUBLIC_ENDPOINTS.any { requestPath.contains(it, ignoreCase = true) }) {
            return chain.proceed(originalRequest)
        }

        // 2. Pega o token usando o método correto do TokenManager
        // O método getToken() já roda um runBlocking interno, então não precisamos dele aqui
        val token = tokenManager.getToken()

        // 3. Se não tem token, segue sem autenticação (provavelmente vai dar 401, mas deixa a API responder)
        if (token.isNullOrEmpty()) {
            return chain.proceed(originalRequest)
        }

        // 4. Reconstrói a requisição adicionando o Header
        val authenticatedRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()

        return chain.proceed(authenticatedRequest)
    }
}