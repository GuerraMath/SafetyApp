package com.guerramath.safetyapp.presentation.evaluation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guerramath.safetyapp.data.repository.SafetyRepository
import com.guerramath.safetyapp.domain.model.SafetyEvaluation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EvaluationViewModel(
    private val repository: SafetyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun submitEvaluation(
        pilotName: String,
        healthScore: Int,
        weatherScore: Int,
        aircraftScore: Int,
        missionScore: Int,
        mitigationPlan: String
    ) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            val result = repository.submitEvaluation(
                pilotName = pilotName,
                healthScore = healthScore,
                weatherScore = weatherScore,
                aircraftScore = aircraftScore,
                missionScore = missionScore,
                mitigationPlan = mitigationPlan
            )

            result.fold(
                onSuccess = { evaluation ->
                    _uiState.value = UiState.Success(evaluation)
                },
                onFailure = { error ->
                    _uiState.value = UiState.Error(
                        error.message ?: "Unknown error occurred"
                    )
                }
            )
        }
    }

    fun resetState() {
        _uiState.value = UiState.Idle
    }

    sealed class UiState {
        object Idle : UiState()
        object Loading : UiState()
        data class Success(val evaluation: SafetyEvaluation) : UiState()
        data class Error(val message: String) : UiState()
    }
}
