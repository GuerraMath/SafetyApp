package com.guerramath.safetyapp.domain.model

data class User(
    val id: String,
    val name: String,
    val email: String,
    val photoUrl: String? = null,
    val role: UserRole = UserRole.PILOT,
    val createdAt: Long = System.currentTimeMillis()
)

enum class UserRole {
    PILOT,
    INSTRUCTOR,
    AUDITOR,
    ADMIN
}