package com.guerramath.safetyapp.auth.data.dto

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("token")
    val token: String,

    @SerializedName("user_id")
    val userId: Int?, // Pode ser null dependendo da sua API

    @SerializedName("name")
    val name: String?
)