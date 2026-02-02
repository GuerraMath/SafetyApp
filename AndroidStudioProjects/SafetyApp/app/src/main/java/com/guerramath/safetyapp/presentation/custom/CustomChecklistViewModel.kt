package com.guerramath.safetyapp.presentation.custom

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.guerramath.safetyapp.data.local.CustomChecklistDao
import com.guerramath.safetyapp.data.local.CustomChecklistEntity
import com.guerramath.safetyapp.domain.model.CustomChecklist
import com.guerramath.safetyapp.domain.model.CustomChecklistItem
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CustomChecklistViewModel(
    private val dao: CustomChecklistDao
) : ViewModel() {

    private val _currentChecklist = MutableStateFlow<CustomChecklist?>(null)
    val currentChecklist: StateFlow<CustomChecklist?> = _currentChecklist.asStateFlow()

    val savedChecklists: Flow<List<CustomChecklist>> = dao.getAllChecklists()
        .map { entities ->
            entities.map { entity ->
                CustomChecklist(
                    id = entity.id,
                    title = entity.title,
                    items = com.google.gson.Gson().fromJson(
                        entity.items,
                        object : com.google.gson.reflect.TypeToken<MutableList<CustomChecklistItem>>() {}.type
                    ),
                    createdAt = entity.createdAt,
                    updatedAt = entity.updatedAt
                )
            }
        }

    fun createNewChecklist(title: String) {
        _currentChecklist.value = CustomChecklist(
            title = title.ifBlank { "Novo Checklist" }
        )
    }

    fun loadChecklist(checklistId: String) {
        viewModelScope.launch {
            val entity = dao.getChecklistById(checklistId)
            if (entity != null) {
                _currentChecklist.value = CustomChecklist(
                    id = entity.id,
                    title = entity.title,
                    items = com.google.gson.Gson().fromJson(
                        entity.items,
                        object : com.google.gson.reflect.TypeToken<MutableList<CustomChecklistItem>>() {}.type
                    ),
                    createdAt = entity.createdAt,
                    updatedAt = entity.updatedAt
                )
            }
        }
    }

    fun updateTitle(title: String) {
        _currentChecklist.value = _currentChecklist.value?.copy(title = title)
    }

    fun addItem(text: String) {
        _currentChecklist.value?.let { checklist ->
            checklist.items.add(CustomChecklistItem(text = text))
            _currentChecklist.value = checklist.copy(
                items = checklist.items,
                updatedAt = System.currentTimeMillis()
            )
        }
    }

    fun removeItem(itemId: String) {
        _currentChecklist.value?.let { checklist ->
            checklist.items.removeAll { it.id == itemId }
            _currentChecklist.value = checklist.copy(
                items = checklist.items,
                updatedAt = System.currentTimeMillis()
            )
        }
    }

    fun toggleItem(itemId: String) {
        _currentChecklist.value?.let { checklist ->
            checklist.items.find { it.id == itemId }?.let { item ->
                item.isChecked = !item.isChecked
                _currentChecklist.value = checklist.copy(
                    items = checklist.items,
                    updatedAt = System.currentTimeMillis()
                )
            }
        }
    }

    fun saveChecklist() {
        viewModelScope.launch {
            _currentChecklist.value?.let { checklist ->
                val entity = CustomChecklistEntity(
                    id = checklist.id,
                    title = checklist.title,
                    items = Gson().toJson(checklist.items),
                    createdAt = checklist.createdAt,
                    updatedAt = System.currentTimeMillis()
                )
                dao.insert(entity)
            }
        }
    }

    fun deleteChecklist(checklistId: String) {
        viewModelScope.launch {
            dao.deleteById(checklistId)
        }
    }
}
