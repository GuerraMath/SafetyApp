package com.guerramath.safetyapp.auth.biometric

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

/**
 * Resultado da autenticação biométrica
 */
sealed class BiometricResult {
    data object Success : BiometricResult()
    data object Cancelled : BiometricResult()
    data class Error(val message: String, val code: Int) : BiometricResult()
    data object NotAvailable : BiometricResult()
    data object NotEnrolled : BiometricResult()
}

/**
 * Status de disponibilidade da biometria
 */
enum class BiometricStatus {
    AVAILABLE,
    NOT_AVAILABLE,
    NOT_ENROLLED,
    HARDWARE_UNAVAILABLE,
    SECURITY_UPDATE_REQUIRED
}

/**
 * Gerenciador de autenticação biométrica.
 * Suporta impressão digital, reconhecimento facial e PIN/padrão do dispositivo.
 */
class BiometricAuthManager(private val context: Context) {

    private val biometricManager = BiometricManager.from(context)

    /**
     * Verifica se a biometria está disponível no dispositivo.
     */
    fun getBiometricStatus(): BiometricStatus {
        return when (biometricManager.canAuthenticate(BIOMETRIC_STRONG or BIOMETRIC_WEAK)) {
            BiometricManager.BIOMETRIC_SUCCESS -> BiometricStatus.AVAILABLE
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> BiometricStatus.NOT_AVAILABLE
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> BiometricStatus.HARDWARE_UNAVAILABLE
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> BiometricStatus.NOT_ENROLLED
            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> BiometricStatus.SECURITY_UPDATE_REQUIRED
            else -> BiometricStatus.NOT_AVAILABLE
        }
    }

    /**
     * Verifica se biometria está disponível e configurada.
     */
    fun isBiometricAvailable(): Boolean {
        return getBiometricStatus() == BiometricStatus.AVAILABLE
    }

    /**
     * Verifica se o dispositivo suporta biometria (mesmo sem estar configurada).
     */
    fun hasBiometricCapability(): Boolean {
        val status = getBiometricStatus()
        return status == BiometricStatus.AVAILABLE || status == BiometricStatus.NOT_ENROLLED
    }

    /**
     * Mostra o prompt de autenticação biométrica.
     *
     * @param activity FragmentActivity necessária para o prompt
     * @param title Título do prompt
     * @param subtitle Subtítulo do prompt
     * @param negativeButtonText Texto do botão negativo (cancelar)
     * @param allowDeviceCredential Se true, permite PIN/padrão como alternativa
     * @param onResult Callback com o resultado da autenticação
     */
    fun authenticate(
        activity: FragmentActivity,
        title: String = "Autenticação Biométrica",
        subtitle: String = "Use sua digital ou rosto para continuar",
        negativeButtonText: String = "Cancelar",
        allowDeviceCredential: Boolean = false,
        onResult: (BiometricResult) -> Unit
    ) {
        // Verifica disponibilidade
        if (!isBiometricAvailable()) {
            val status = getBiometricStatus()
            when (status) {
                BiometricStatus.NOT_ENROLLED -> onResult(BiometricResult.NotEnrolled)
                else -> onResult(BiometricResult.NotAvailable)
            }
            return
        }

        val executor = ContextCompat.getMainExecutor(context)

        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onResult(BiometricResult.Success)
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                // Não chamamos callback aqui - o sistema mostra mensagem automaticamente
                // O usuário pode tentar novamente
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)

                val result = when (errorCode) {
                    BiometricPrompt.ERROR_USER_CANCELED,
                    BiometricPrompt.ERROR_NEGATIVE_BUTTON,
                    BiometricPrompt.ERROR_CANCELED -> BiometricResult.Cancelled

                    BiometricPrompt.ERROR_NO_BIOMETRICS -> BiometricResult.NotEnrolled

                    BiometricPrompt.ERROR_HW_NOT_PRESENT,
                    BiometricPrompt.ERROR_HW_UNAVAILABLE -> BiometricResult.NotAvailable

                    else -> BiometricResult.Error(errString.toString(), errorCode)
                }

                onResult(result)
            }
        }

        val biometricPrompt = BiometricPrompt(activity, executor, callback)

        val promptInfoBuilder = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)

        if (allowDeviceCredential) {
            // Permite PIN/padrão como alternativa
            promptInfoBuilder.setAllowedAuthenticators(
                BIOMETRIC_STRONG or BIOMETRIC_WEAK or DEVICE_CREDENTIAL
            )
        } else {
            // Apenas biometria
            promptInfoBuilder
                .setNegativeButtonText(negativeButtonText)
                .setAllowedAuthenticators(BIOMETRIC_STRONG or BIOMETRIC_WEAK)
        }

        val promptInfo = promptInfoBuilder.build()

        biometricPrompt.authenticate(promptInfo)
    }
}

