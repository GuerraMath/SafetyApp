package com.guerramath.safetyapp.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guerramath.safetyapp.data.repository.SafetyRepository
import com.guerramath.safetyapp.domain.model.SafetyEvaluation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HistoryViewModel(
    private val repository: SafetyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        loadHistory()
    }

    fun loadHistory() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                repository.getAllEvaluations().collect { evaluations ->
                    _uiState.value = if (evaluations.isEmpty()) {
                        UiState.Empty
                    } else {
                        UiState.Success(evaluations)
                    }
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    sealed class UiState {
        object Loading : UiState()
        object Empty : UiState()
        data class Success(val evaluations: List<SafetyEvaluation>) : UiState()
        data class Error(val message: String) : UiState()
    }
}
