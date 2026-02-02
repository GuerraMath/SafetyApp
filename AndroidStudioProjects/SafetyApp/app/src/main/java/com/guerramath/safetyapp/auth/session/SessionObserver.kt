
package com.guerramath.safetyapp.auth.session

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Evento de sessão que pode ser observado globalmente.
 */
sealed class SessionEvent {
    /** Sessão expirou - usuário precisa relogar */
    data object SessionExpired : SessionEvent()

    /** Usuário fez logout manualmente */
    data object LoggedOut : SessionEvent()

    /** Erro de autenticação genérico */
    data class AuthError(val message: String) : SessionEvent()

    /** Token foi renovado com sucesso */
    data object TokenRefreshed : SessionEvent()
}

/**
 * Observador global de eventos de sessão.
 * Use para reagir a logout/expiração de qualquer lugar do app.
 *
 * Exemplo de uso na MainActivity:
 * ```
 * lifecycleScope.launch {
 *     SessionObserver.events.collect { event ->
 *         when (event) {
 *             is SessionEvent.SessionExpired -> navigateToLogin()
 *             is SessionEvent.LoggedOut -> navigateToLogin()
 *             else -> {}
 *         }
 *     }
 * }
 * ```
 */
object SessionObserver {

    private val _events = MutableSharedFlow<SessionEvent>(
        replay = 0,
        extraBufferCapacity = 1
    )

    /** Flow de eventos de sessão */
    val events: SharedFlow<SessionEvent> = _events.asSharedFlow()

    /** Emite evento de sessão expirada */
    suspend fun notifySessionExpired() {
        _events.emit(SessionEvent.SessionExpired)
    }

    /** Emite evento de logout */
    suspend fun notifyLoggedOut() {
        _events.emit(SessionEvent.LoggedOut)
    }

    /** Emite evento de erro de autenticação */
    suspend fun notifyAuthError(message: String) {
        _events.emit(SessionEvent.AuthError(message))
    }

    /** Emite evento de token renovado */
    suspend fun notifyTokenRefreshed() {
        _events.emit(SessionEvent.TokenRefreshed)
    }

    // Versões síncronas (fire and forget)
    fun emitSessionExpired() {
        _events.tryEmit(SessionEvent.SessionExpired)
    }

    fun emitLoggedOut() {
        _events.tryEmit(SessionEvent.LoggedOut)
    }

    fun emitAuthError(message: String) {
        _events.tryEmit(SessionEvent.AuthError(message))
    }
}

