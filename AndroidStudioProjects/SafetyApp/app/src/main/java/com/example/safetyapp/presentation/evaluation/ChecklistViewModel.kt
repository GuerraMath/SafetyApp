package com.guerramath.safetyapp.presentation.evaluation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guerramath.safetyapp.data.repository.SafetyRepository
import com.guerramath.safetyapp.domain.model.ChecklistCategory
import com.guerramath.safetyapp.domain.model.ChecklistItem
import com.guerramath.safetyapp.domain.model.SafetyEvaluation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChecklistViewModel(
    private val repository: SafetyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _categories = MutableStateFlow<List<ChecklistCategory>>(emptyList())
    val categories: StateFlow<List<ChecklistCategory>> = _categories.asStateFlow()

    init {
        loadChecklist()
    }

    private fun loadChecklist() {
        _categories.value = listOf(
            ChecklistCategory(
                id = "health",
                title = "SAÚDE",
                emoji = "❤️",
                description = "Fatores Humanos",
                items = mutableListOf(
                    ChecklistItem("health_1", "Repouso adequado (8h)?"),
                    ChecklistItem("health_2", "Hidratação/Alimentação?"),
                    ChecklistItem("health_3", "Nível de estresse/Fadiga?"),
                    ChecklistItem("health_4", "Medicamentos ou Álcool?"),
                    ChecklistItem("health_5", "Equipamento (EPI) completo?")
                )
            ),
            ChecklistCategory(
                id = "weather",
                title = "METEOROLOGIA",
                emoji = "☁️",
                description = "Ambiente",
                items = mutableListOf(
                    ChecklistItem("weather_1", "Vento dentro do envelope?"),
                    ChecklistItem("weather_2", "Visibilidade/Teto?"),
                    ChecklistItem("weather_3", "Temperatura/Umidade?"),
                    ChecklistItem("weather_4", "Previsão de mudança?"),
                    ChecklistItem("weather_5", "Turbulência esperada?")
                )
            ),
            ChecklistCategory(
                id = "aircraft",
                title = "AERONAVE",
                emoji = "✈️",
                description = "Máquina",
                items = mutableListOf(
                    ChecklistItem("aircraft_1", "Combustível suficiente?"),
                    ChecklistItem("aircraft_2", "Peso e Balanceamento?"),
                    ChecklistItem("aircraft_3", "Sistemas de pulverização?"),
                    ChecklistItem("aircraft_4", "Manutenção em dia?"),
                    ChecklistItem("aircraft_5", "Performance para pista?")
                )
            ),
            ChecklistCategory(
                id = "mission",
                title = "MISSÃO",
                emoji = "🎯",
                description = "Operação",
                items = mutableListOf(
                    ChecklistItem("mission_1", "Obstáculos mapeados?"),
                    ChecklistItem("mission_2", "Comunicação solo-ar?"),
                    ChecklistItem("mission_3", "Pressão de tempo?"),
                    ChecklistItem("mission_4", "Plano de contingência?"),
                    ChecklistItem("mission_5", "Áreas sensíveis próximas?")
                )
            )
        )
    }

    fun updateChecklistItem(categoryId: String, itemId: String, isChecked: Boolean) {
        _categories.value = _categories.value.map { category ->
            if (category.id == categoryId) {
                category.copy(
                    items = category.items.map { item ->
                        if (item.id == itemId) item.copy(isChecked = isChecked)
                        else item
                    }.toMutableList()
                )
            } else category
        }
    }

    fun updateComment(categoryId: String, itemId: String, comment: String) {
        _categories.value = _categories.value.map { category ->
            if (category.id == categoryId) {
                category.copy(
                    items = category.items.map { item ->
                        if (item.id == itemId) item.copy(comment = comment)
                        else item
                    }.toMutableList()
                )
            } else category
        }
    }

    fun calculateScores(): Map<String, Int> {
        val scores = mutableMapOf<String, Int>()
        _categories.value.forEach { category ->
            val checkedCount = category.items.count { it.isChecked }
            // Score: 5 - número de itens não checados (inverso)
            scores[category.id] = 5 - (5 - checkedCount)
        }
        return scores
    }

    fun submitEvaluation(pilotName: String, mitigationPlan: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            val scores = calculateScores()
            val result = repository.submitEvaluation(
                pilotName = pilotName,
                healthScore = scores["health"] ?: 3,
                weatherScore = scores["weather"] ?: 3,
                aircraftScore = scores["aircraft"] ?: 3,
                missionScore = scores["mission"] ?: 3,
                mitigationPlan = buildDetailedMitigationPlan(mitigationPlan)
            )

            result.fold(
                onSuccess = { evaluation ->
                    _uiState.value = UiState.Success(evaluation)
                },
                onFailure = { error ->
                    _uiState.value = UiState.Error(error.message ?: "Erro desconhecido")
                }
            )
        }
    }

    private fun buildDetailedMitigationPlan(basePlan: String): String {
        val comments = mutableListOf<String>()
        _categories.value.forEach { category ->
            category.items.forEach { item ->
                if (item.comment.isNotBlank()) {
                    comments.add("${category.title} - ${item.text}: ${item.comment}")
                }
            }
        }

        return if (comments.isNotEmpty()) {
            "$basePlan\n\nDetalhes:\n${comments.joinToString("\n")}"
        } else {
            basePlan
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
