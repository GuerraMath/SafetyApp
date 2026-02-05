package com.guerramath.safetyapp.auth.oauth

import android.content.Context
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resumeWithException

class GoogleSignInHelper(context: Context, clientId: String) {
    private val googleSignInClient: GoogleSignInClient
    private val tag = "GoogleSignInHelper"

    init {
        Log.d(tag, "Inicializando GoogleSignInHelper com clientId: ${clientId.take(20)}...")

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(clientId)
            .requestEmail()
            .requestProfile()
            .build()

        googleSignInClient = GoogleSignIn.getClient(context, gso)
        Log.d(tag, "GoogleSignInClient inicializado com sucesso")
    }

    fun getSignInIntent() {
        Log.d(tag, "Retornando intent de sign-in")
        return googleSignInClient.signInIntent
    }

    suspend fun getIdToken(task: Task<com.google.android.gms.auth.api.signin.GoogleSignInAccount>): String {
        return suspendCancellableCoroutine { continuation ->
            try {
                Log.d(tag, "Processando resultado do Google Sign-In...")
                val account = task.getResult(ApiException::class.java)
                val idToken = account?.idToken

                if (idToken != null) {
                    Log.d(tag, "ID Token obtido com sucesso (${idToken.length} chars)")
                    continuation.resume(idToken)
                } else {
                    val errorMsg = "ID Token é null. Verifique se os scopes estão corretos."
                    Log.e(tag, errorMsg)
                    continuation.resumeWithException(Exception(errorMsg))
                }
            } catch (e: ApiException) {
                val errorMsg = GoogleSignInErrorHandler.getErrorMessage(e)
                GoogleSignInErrorHandler.logDetailedError(e)
                Log.e(tag, errorMsg, e)
                continuation.resumeWithException(e)
            } catch (e: Exception) {
                Log.e(tag, "Erro inesperado ao processar Google Sign-In", e)
                continuation.resumeWithException(e)
            }
        }
    }

    fun signOut() {
        Log.d(tag, "Assinando fora do Google Sign-In")
        googleSignInClient.signOut()
    }
}
