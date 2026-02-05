package com.guerramath.safetyapp.auth.oauth

import android.util.Log
import com.google.android.gms.common.api.ApiException

/**
 * Helper para traduzir erros do Google Sign-In para mensagens em português
 */
object GoogleSignInErrorHandler {
    private const val TAG = "GoogleSignInError"

    /**
     * Obtém mensagem de erro traduzida baseado no código de erro do Google
     */
    fun getErrorMessage(exception: ApiException): String {
        val message = when (exception.statusCode) {
            12500 -> "Erro ao conectar com Google Play Services"
            12501 -> "Google Sign-In foi cancelado pelo usuário"
            12502 -> "Google Play Services não está disponível ou atualizado. Por favor, atualize o Google Play Services"
            12503 -> "Erro ao conectar com Google Sign-In"
            28444 -> "Developer Console não configurado corretamente. Verifique:\n• OAuth Consent Screen está habilitado\n• Redirect URIs estão corretos\n• SHA-1 do keystore está registrado\n• Google+ API está ativada"
            else -> "Erro no Google Sign-In: ${exception.message}"
        }

        Log.e(TAG, "Erro Google Sign-In [${exception.statusCode}]: $message", exception)
        return message
    }

    /**
     * Log detalhado de erros para debug
     */
    fun logDetailedError(exception: ApiException) {
        Log.e(
            TAG,
            """
            ========== ERRO DETALHADO GOOGLE SIGN-IN ==========
            StatusCode: ${exception.statusCode}
            Message: ${exception.message}
            Cause: ${exception.cause}
            StackTrace: ${exception.stackTraceToString()}
            ====================================================
            """.trimIndent()
        )
    }

    /**
     * Valida se é um erro relacionado a configuração do console
     */
    fun isConfigurationError(exception: ApiException): Boolean {
        return exception.statusCode == 28444
    }

    /**
     * Valida se é um erro relacionado a cancelamento do usuário
     */
    fun isCanceledByUser(exception: ApiException): Boolean {
        return exception.statusCode == 12501
    }

    /**
     * Valida se é um erro relaciona a Google Play Services
     */
    fun isPlayServicesError(exception: ApiException): Boolean {
        return exception.statusCode in listOf(12500, 12502, 12503)
    }
}
