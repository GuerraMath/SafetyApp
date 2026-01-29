package com.guerramath.safetyapp.data.api.dto

import com.google.gson.annotations.SerializedName

data class SafetyEvaluationRequest(
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

    @SerializedName("mitigationPlan")
    val mitigationPlan: String
)