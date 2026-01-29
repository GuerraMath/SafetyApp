package com.guerramath.safetyapp.data.repository

import com.guerramath.safetyapp.data.api.SafetyApiService
import com.guerramath.safetyapp.data.api.dto.SafetyEvaluationRequest
import com.guerramath.safetyapp.data.api.dto.SafetyEvaluationResponse
import com.guerramath.safetyapp.data.local.SafetyDao
import com.guerramath.safetyapp.data.local.SafetyEntity
import com.guerramath.safetyapp.domain.model.SafetyEvaluation
import com.guerramath.safetyapp.domain.model.RiskLevel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.io.IOException

class SafetyRepository(
    private val api: SafetyApiService,
    private val dao: SafetyDao
) {

    suspend fun submitEvaluation(
        pilotName: String,
        healthScore: Int,
        weatherScore: Int,
        aircraftScore: Int,
        missionScore: Int,
        mitigationPlan: String
    ): Result<SafetyEvaluation> {
        return try {
            val request = SafetyEvaluationRequest(
                pilotName = pilotName,
                healthScore = healthScore,
                weatherScore = weatherScore,
                aircraftScore = aircraftScore,
                missionScore = missionScore,
                mitigationPlan = mitigationPlan
            )

            val response = api.submitEvaluation(request)

            if (response.isSuccessful && response.body() != null) {
                val data = response.body()!!
                // Cache localmente
                dao.insert(data.toEntity())
                Result.success(data.toDomain())
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: IOException) {
            Result.failure(Exception("Network error: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getHistory(pilotName: String): Flow<List<SafetyEvaluation>> {
        return flow {
            // Emite cache primeiro
            dao.getEvaluationsByPilot(pilotName).collect { cached ->
                emit(cached.map { it.toDomain() })

                // Tenta atualizar da API
                try {
                    val response = api.getHistory(pilotName)
                    if (response.isSuccessful && response.body() != null) {
                        val remote = response.body()!!
                        dao.insertAll(remote.map { it.toEntity() })
                        emit(remote.map { it.toDomain() })
                    }
                } catch (e: Exception) {
                    // Mantém cache se falhar
                }
            }
        }
    }

    fun getAllEvaluations(): Flow<List<SafetyEvaluation>> {
        return dao.getAllEvaluations().map { list ->
            list.map { it.toDomain() }
        }
    }

    // Extension functions para conversão
    private fun SafetyEvaluationResponse.toEntity() = SafetyEntity(
        id = id,
        pilotName = pilotName,
        healthScore = healthScore,
        weatherScore = weatherScore,
        aircraftScore = aircraftScore,
        missionScore = missionScore,
        riskLevel = riskLevel,
        totalScore = totalScore,
        timestamp = timestamp,
        mitigationPlan = mitigationPlan
    )

    private fun SafetyEvaluationResponse.toDomain() = SafetyEvaluation(
        id = id,
        pilotName = pilotName,
        healthScore = healthScore,
        weatherScore = weatherScore,
        aircraftScore = aircraftScore,
        missionScore = missionScore,
        riskLevel = RiskLevel.valueOf(riskLevel),
        totalScore = totalScore,
        timestamp = timestamp,
        mitigationPlan = mitigationPlan
    )

    private fun SafetyEntity.toDomain() = SafetyEvaluation(
        id = id,
        pilotName = pilotName,
        healthScore = healthScore,
        weatherScore = weatherScore,
        aircraftScore = aircraftScore,
        missionScore = missionScore,
        riskLevel = RiskLevel.valueOf(riskLevel),
        totalScore = totalScore,
        timestamp = timestamp,
        mitigationPlan = mitigationPlan
    )
}