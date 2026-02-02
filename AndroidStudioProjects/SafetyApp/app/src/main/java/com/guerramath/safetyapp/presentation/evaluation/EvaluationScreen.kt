package com.guerramath.safetyapp.presentation.evaluation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.guerramath.safetyapp.domain.model.SafetyEvaluation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EvaluationScreen(
    viewModel: EvaluationViewModel,
    onNavigateToHistory: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    var pilotName by remember { mutableStateOf("") }
    var healthScore by remember { mutableStateOf(1) }
    var weatherScore by remember { mutableStateOf(1) }
    var aircraftScore by remember { mutableStateOf(1) }
    var missionScore by remember { mutableStateOf(1) }
    var mitigationPlan by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Safety Evaluation") },
                actions = {
                    IconButton(onClick = onNavigateToHistory) {
                        Icon(Icons.Default.History, "History")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Pilot Name
            OutlinedTextField(
                value = pilotName,
                onValueChange = { pilotName = it },
                label = { Text("Pilot Name") },
                leadingIcon = { Icon(Icons.Default.Person, null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Score Sliders
            ScoreSlider(
                title = "Health Score",
                value = healthScore,
                onValueChange = { healthScore = it },
                icon = Icons.Default.Favorite
            )

            ScoreSlider(
                title = "Weather Score",
                value = weatherScore,
                onValueChange = { weatherScore = it },
                icon = Icons.Default.Cloud
            )

            ScoreSlider(
                title = "Aircraft Score",
                value = aircraftScore,
                onValueChange = { aircraftScore = it },
                icon = Icons.Default.Flight
            )

            ScoreSlider(
                title = "Mission Score",
                value = missionScore,
                onValueChange = { missionScore = it },
                icon = Icons.Default.Flag
            )

            // Mitigation Plan
            OutlinedTextField(
                value = mitigationPlan,
                onValueChange = { mitigationPlan = it },
                label = { Text("Mitigation Plan") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )

            // Submit Button
            Button(
                onClick = {
                    if (pilotName.isNotBlank()) {
                        viewModel.submitEvaluation(
                            pilotName, healthScore, weatherScore,
                            aircraftScore, missionScore, mitigationPlan
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState !is EvaluationViewModel.UiState.Loading && pilotName.isNotBlank()
            ) {
                if (uiState is EvaluationViewModel.UiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Submit Evaluation")
                }
            }

            // Result Display
            when (val state = uiState) {
                is EvaluationViewModel.UiState.Success -> {
                    ResultCard(state.evaluation) { viewModel.resetState() }
                }
                is EvaluationViewModel.UiState.Error -> {
                    ErrorCard(state.message) { viewModel.resetState() }
                }
                else -> {}
            }
        }
    }
}

@Composable
fun ScoreSlider(
    title: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(icon, null, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = value.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            Slider(
                value = value.toFloat(),
                onValueChange = { onValueChange(it.toInt()) },
                valueRange = 1f..5f,
                steps = 3,
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("1 (Best)", style = MaterialTheme.typography.bodySmall)
                Text("5 (Worst)", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun ResultCard(evaluation: SafetyEvaluation, onDismiss: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = evaluation.riskLevel.color.copy(alpha = 0.2f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Evaluation Complete",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, "Dismiss")
                }
            }
            Divider()
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Risk Level:")
                Text(
                    evaluation.riskLevel.displayName,
                    fontWeight = FontWeight.Bold,
                    color = evaluation.riskLevel.color
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Total Score:")
                Text("${evaluation.totalScore}", fontWeight = FontWeight.Bold)
            }
            if (evaluation.mitigationPlan?.isNotBlank() == true) {
                Divider()
                Text("Mitigation Plan:", fontWeight = FontWeight.Bold)
                Text(evaluation.mitigationPlan)
            }
        }
    }
}

@Composable
fun ErrorCard(message: String, onDismiss: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(Icons.Default.Error, "Error", tint = MaterialTheme.colorScheme.error)
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, "Dismiss")
                }
            }
            Text("Error", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(message)
        }
    }
}