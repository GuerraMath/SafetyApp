package com.guerramath.safetyapp.auth.oauth

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resumeWithException

class GoogleSignInHelper(context: Context, clientId: String) {
    private val googleSignInClient: GoogleSignInClient

    init {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(clientId)
            .requestEmail()
            .requestProfile()
            .build()

        googleSignInClient = GoogleSignIn.getClient(context, gso)
    }

    fun getSignInIntent() = googleSignInClient.signInIntent

    suspend fun getIdToken(task: Task<com.google.android.gms.auth.api.signin.GoogleSignInAccount>): String {
        return suspendCancellableCoroutine { continuation ->
            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account?.idToken
                if (idToken != null) {
                    continuation.resume(idToken)
                } else {
                    continuation.resumeWithException(Exception("ID Token is null"))
                }
            } catch (e: ApiException) {
                continuation.resumeWithException(e)
            }
        }
    }

    fun signOut() {
        googleSignInClient.signOut()
    }
}
