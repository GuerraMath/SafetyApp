package com.guerramath.safetyapp.domain.model

data class SafetyEvaluation(
    val id: Long,
    val pilotName: String,
    val healthScore: Int,
    val weatherScore: Int,
    val aircraftScore: Int,
    val missionScore: Int,
    val riskLevel: RiskLevel,
    val totalScore: Int,
    val timestamp: String,
    val mitigationPlan: String?
)