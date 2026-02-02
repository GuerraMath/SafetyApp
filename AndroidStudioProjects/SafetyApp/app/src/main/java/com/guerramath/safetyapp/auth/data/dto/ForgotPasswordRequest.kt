package com.guerramath.safetyapp.auth.data.dto

import com.google.gson.annotations.SerializedName

data class ForgotPasswordRequest(
    @SerializedName("email")
    val email: String
)