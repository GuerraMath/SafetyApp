package com.guerramath.safetyapp.data.api.dto

import com.google.gson.annotations.SerializedName

data class SafetyEvaluationResponse(
    @SerializedName("id")
    val id: Long,

    @SerializedName("pilotName")
    val pilotName: String,

    @SerializedName("healthScore")
    val healthScore: Int,

    @SerializedName("weatherScore")
    val weatherScore: Int,

    @SerializedName("aircraftScore")
    val aircraftScore: Int,

    @SerializedName("missionScore")
    val missionScore: Int,

    @SerializedName("riskLevel")
    val riskLevel: String,

    @SerializedName("totalScore")
    val totalScore: Int,

    @SerializedName("timestamp")
    val timestamp: String,

    @SerializedName("mitigationPlan")
    val mitigationPlan: String?
)