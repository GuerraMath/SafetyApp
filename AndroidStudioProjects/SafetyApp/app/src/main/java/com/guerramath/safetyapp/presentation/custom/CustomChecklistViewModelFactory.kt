package com.guerramath.safetyapp.presentation.custom

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.guerramath.safetyapp.data.local.CustomChecklistDao

class CustomChecklistViewModelFactory(
    private val dao: CustomChecklistDao
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CustomChecklistViewModel::class.java)) {
            return CustomChecklistViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}