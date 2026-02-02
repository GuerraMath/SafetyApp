package com.guerramath.safetyapp.auth.data.dto

import com.google.gson.annotations.SerializedName
import com.guerramath.safetyapp.auth.data.model.User

data class AuthResponse(
    @SerializedName("token")
    val token: String,

    @SerializedName("refresh_token")
    val refreshToken: String,

    @SerializedName("user")
    val user: User // Tem que ser User
)