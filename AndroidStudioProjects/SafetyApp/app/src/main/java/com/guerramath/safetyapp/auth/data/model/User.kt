package com.guerramath.safetyapp.auth.data.model

import com.google.gson.annotations.SerializedName
import com.guerramath.safetyapp.domain.model.UserRole

data class User(
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("avatar_url")
    val avatarUrl: String? = null,

    @SerializedName("role")
    val role: UserRole = UserRole.PILOT,

    @SerializedName("email_verified")
    val emailVerified: Boolean = false,

    @SerializedName("created_at")
    val createdAt: String? = null
)
