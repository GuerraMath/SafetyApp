package com.guerramath.safetyapp.auth.data.google

import android.app.Activity
import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Resultado do login com Google
 */
sealed class GoogleSignInResult {
    data class Success(
        val idToken: String,
        val email: String,
        val displayName: String?,
        val profilePictureUrl: String?
    ) : GoogleSignInResult()

    data class Error(val message: String) : GoogleSignInResult()
    object Cancelled : GoogleSignInResult()
}

/**
 * Gerenciador de autenticação com Google para a SafetyApp.
 *
 * @param context Application context para criar o CredentialManager
 */
class GoogleAuthManager(private val context: Context) {

    private val credentialManager = CredentialManager.create(context)

    companion object {
        // Web Client ID do Google Cloud Console


        const val WEB_CLIENT_ID = "727557341501-46tnut2rjt967ioctg618m7c42hctsnh.apps.googleusercontent.com"


    }

    /**
     * Inicia o fluxo de login com Google.
     *
     * @param activity A Activity atual necessária para mostrar o diálogo de seleção de conta
     */
    suspend fun signIn(activity: Activity): GoogleSignInResult = withContext(Dispatchers.Main) {
        try {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(WEB_CLIENT_ID)
                .setAutoSelectEnabled(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(
                request = request,
                context = activity
            )

            handleSignInResult(result)

        } catch (e: GetCredentialCancellationException) {
            GoogleSignInResult.Cancelled
        } catch (e: NoCredentialException) {
            GoogleSignInResult.Error("Nenhuma conta Google encontrada no dispositivo.")
        } catch (e: GetCredentialException) {
            GoogleSignInResult.Error("Erro de credencial: ${e.message}")
        } catch (e: Exception) {
            GoogleSignInResult.Error("Erro inesperado: ${e.message}")
        }
    }

    private fun handleSignInResult(result: GetCredentialResponse): GoogleSignInResult {
        val credential = result.credential

        return when (credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

                        GoogleSignInResult.Success(
                            idToken = googleIdTokenCredential.idToken,
                            email = googleIdTokenCredential.id,
                            displayName = googleIdTokenCredential.displayName,
                            profilePictureUrl = googleIdTokenCredential.profilePictureUri?.toString()
                        )
                    } catch (e: GoogleIdTokenParsingException) {
                        GoogleSignInResult.Error("Erro ao processar token: ${e.message}")
                    }
                } else {
                    GoogleSignInResult.Error("Tipo de credencial não suportado")
                }
            }
            else -> GoogleSignInResult.Error("Credencial inválida")
        }
    }
}