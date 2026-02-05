package com.guerramath.safetyapp.auth.data.api

import com.guerramath.safetyapp.auth.data.dto.AuthResponse
import com.guerramath.safetyapp.auth.data.dto.ForgotPasswordRequest
import com.guerramath.safetyapp.auth.data.dto.GoogleSignInRequest
import com.guerramath.safetyapp.auth.data.dto.LoginRequest
import com.guerramath.safetyapp.auth.data.dto.MessageResponse
import com.guerramath.safetyapp.auth.data.dto.OAuthLoginRequest
import com.guerramath.safetyapp.auth.data.dto.RefreshTokenRequest
import com.guerramath.safetyapp.auth.data.dto.RegisterRequest
import com.guerramath.safetyapp.auth.data.model.User // Correct Import
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApiService {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("auth/oauth/login")
    suspend fun oauthLogin(@Body request: OAuthLoginRequest): Response<AuthResponse>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<MessageResponse>

    @POST("auth/refresh")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): Response<AuthResponse>

    @GET("auth/me")
    suspend fun getCurrentUser(@Header("Authorization") token: String): Response<User>

    // FIX: Adding the missing logout function
    @POST("auth/logout")
    suspend fun logout(): Response<Unit>

    /**
     * Autenticação via Google.
     * Envia o ID Token do Google para o backend validar e criar/autenticar o usuário.
     */
    @POST("auth/google")
    suspend fun googleSignIn(@Body request: GoogleSignInRequest): Response<AuthResponse>
}