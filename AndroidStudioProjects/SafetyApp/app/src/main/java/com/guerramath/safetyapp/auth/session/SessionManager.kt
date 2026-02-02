package com.guerramath.safetyapp.auth.session

import android.content.Context
import com.guerramath.safetyapp.auth.data.token.TokenManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

object SessionManager {

    private var tokenManager: TokenManager? = null

    /**
     * Inicializa o SessionManager (chamar no Application.onCreate)
     */
    fun init(context: Context) {
        tokenManager = TokenManager(context)
    }

    /**
     * Flow que emite true quando usuário está logado.
     * Agora funciona porque adicionamos 'isLoggedInFlow' no TokenManager.
     */
    val isLoggedIn: Flow<Boolean>
        get() = tokenManager?.isLoggedInFlow ?: emptyFlow()

    /**
     * Verifica se está logado de forma síncrona.
     * Alterado para usar 'getToken()' em vez de 'getAccessTokenSync()'.
     */
    fun isLoggedInSync(): Boolean {
        return tokenManager?.getToken() != null
    }

    /**
     * Faz logout limpando tokens (suspend)
     */
    suspend fun logout() {
        tokenManager?.clearTokens()
    }

    /**
     * Faz logout de forma síncrona
     */
    fun logoutSync() {
        tokenManager?.clearTokensSync()
    }
}