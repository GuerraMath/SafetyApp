package com.guerramath.safetyapp.auth.data.token

import android.content.Context
import com.guerramath.safetyapp.auth.data.preferences.AuthPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking

class TokenManager(context: Context) {

    private val authPreferences = AuthPreferences(context)

    val isLoggedInFlow: Flow<Boolean> get() = authPreferences.isLoggedIn

    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        authPreferences.saveTokens(accessToken, refreshToken)
    }

    /** Retorna Access Token (Síncrono) */
    fun getToken(): String? {
        return runBlocking {
            authPreferences.getAccessToken()
        }
    }

    /** * Retorna Refresh Token (Síncrono) - NOVO
     * Necessário para o TokenRefreshInterceptor
     */
    fun getRefreshToken(): String? {
        return runBlocking {
            authPreferences.refreshToken.firstOrNull()
        }
    }

    suspend fun clearTokens() {
        authPreferences.clearAuthData()
    }

    fun clearTokensSync() {
        runBlocking {
            authPreferences.clearAuthData()
        }
    }
}