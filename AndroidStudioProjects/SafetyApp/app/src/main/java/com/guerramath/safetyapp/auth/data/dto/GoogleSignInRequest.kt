package com.guerramath.safetyapp.auth.data.dto

import com.google.gson.annotations.SerializedName

/**
 * Request para autenticação via Google.
 * Envia o ID Token do Google para o backend validar.
 */
data class GoogleSignInRequest(
    @SerializedName("id_token")
    val idToken: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("name")
    val name: String?,

    @SerializedName("avatar_url")
    val avatarUrl: String?
)
