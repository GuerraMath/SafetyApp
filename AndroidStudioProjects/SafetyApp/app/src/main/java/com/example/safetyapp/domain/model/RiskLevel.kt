package com.guerramath.safetyapp.domain.model

import androidx.compose.ui.graphics.Color

enum class RiskLevel(val displayName: String, val color: Color) {
    LOW("Low Risk", Color(0xFF10B981)),      // Verde
    MEDIUM("Medium Risk", Color(0xFFF59E0B)), // Amarelo
    HIGH("High Risk", Color(0xFFEF4444))      // Vermelho
}
