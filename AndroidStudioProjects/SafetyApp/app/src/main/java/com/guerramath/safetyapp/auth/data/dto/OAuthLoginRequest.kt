package com.guerramath.safetyapp.auth.data.dto

data class OAuthLoginRequest(
    val idToken: String,
    val provider: String = "google"
)
