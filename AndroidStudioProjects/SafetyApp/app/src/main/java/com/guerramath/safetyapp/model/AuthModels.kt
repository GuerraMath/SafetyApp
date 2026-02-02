package com.guerramath.safetyapp.data.model

import com.google.gson.annotations.SerializedName

data class AuthRequest(
    @SerializedName("email")
    val email: String,

    @SerializedName("password")
    val password: String,

    @SerializedName("name")
    val name: String? = null
)

data class AuthResponse(
    @SerializedName("token")
    val token: String,

    @SerializedName("user_id")
    val userId: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("message")
    val message: String? = null
)