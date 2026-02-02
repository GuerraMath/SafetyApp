package com.guerramath.safetyapp.data.api

import com.guerramath.safetyapp.data.api.dto.SafetyEvaluationRequest
import com.guerramath.safetyapp.data.api.dto.SafetyEvaluationResponse
import retrofit2.Response
import retrofit2.http.*

interface SafetyApiService {

    @POST("api/v1/safety")
    suspend fun submitEvaluation(
        @Body request: SafetyEvaluationRequest
    ): Response<SafetyEvaluationResponse>

    @GET("api/v1/safety/history")
    suspend fun getHistory(
        @Query("pilotName") pilotName: String
    ): Response<List<SafetyEvaluationResponse>>

    @GET("api/v1/safety/{id}")
    suspend fun getEvaluationById(
        @Path("id") id: Long
    ): Response<SafetyEvaluationResponse>
}