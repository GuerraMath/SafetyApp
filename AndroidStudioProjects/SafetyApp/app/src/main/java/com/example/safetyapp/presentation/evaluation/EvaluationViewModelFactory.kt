package com.guerramath.safetyapp.presentation.evaluation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.guerramath.safetyapp.data.repository.SafetyRepository

class EvaluationViewModelFactory(
    private val repository: SafetyRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EvaluationViewModel::class.java)) {
            return EvaluationViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
