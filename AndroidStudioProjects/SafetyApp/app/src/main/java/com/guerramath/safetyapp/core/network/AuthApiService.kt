package com.guerramath.safetyapp.core.network

import android.content.Context
import com.guerramath.safetyapp.data.model.AuthRequest
import com.guerramath.safetyapp.data.model.AuthResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    @POST("api/auth/login")
    suspend fun login(@Body request: AuthRequest): Response<AuthResponse>

    @POST("api/auth/register")
    suspend fun register(@Body request: AuthRequest): Response<AuthResponse>

    @POST("api/auth/logout")
    suspend fun logout(): Response<Void>

    companion object {
        private const val BASE_URL = "https://your-api-url.com/"

        fun create(context: Context): AuthApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(AuthApiService::class.java)
        }
    }
}