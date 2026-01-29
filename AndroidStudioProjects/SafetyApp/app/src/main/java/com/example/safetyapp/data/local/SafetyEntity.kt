package com.guerramath.safetyapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "safety_evaluations")
data class SafetyEntity(
    @PrimaryKey
    val id: Long,
    val pilotName: String,
    val healthScore: Int,
    val weatherScore: Int,
    val aircraftScore: Int,
    val missionScore: Int,
    val riskLevel: String,
    val totalScore: Int,
    val timestamp: String,
    val mitigationPlan: String?
)