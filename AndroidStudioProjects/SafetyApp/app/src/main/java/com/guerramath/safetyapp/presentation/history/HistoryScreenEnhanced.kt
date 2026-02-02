package com.guerramath.safetyapp.presentation.history

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.guerramath.safetyapp.domain.model.SafetyEvaluation
import com.guerramath.safetyapp.domain.model.RiskLevel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreenEnhanced(
    viewModel: HistoryViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showFilters by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("Todos") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Histórico de Avaliações",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Auditorias de SMS",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Voltar")
                    }
                },
                actions = {
                    IconButton(onClick = { showFilters = !showFilters }) {
                        Icon(Icons.Default.FilterList, "Filtros")
                    }
                    IconButton(onClick = { viewModel.loadHistory() }) {
                        Icon(Icons.Default.Refresh, "Atualizar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Filters
            AnimatedVisibility(visible = showFilters) {
                FilterSection(
                    selectedFilter = selectedFilter,
                    onFilterSelected = { selectedFilter = it }
                )
            }

            // Content
            when (val state = uiState) {
                is HistoryViewModel.UiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(48.dp),
                                color = Color(0xFF0EA5E9)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "Carregando histórico...",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                is HistoryViewModel.UiState.Empty -> {
                    EmptyHistoryState()
                }

                is HistoryViewModel.UiState.Success -> {
                    val filteredEvaluations = when (selectedFilter) {
                        "Baixo" -> state.evaluations.filter { it.riskLevel == RiskLevel.LOW }
                        "Médio" -> state.evaluations.filter { it.riskLevel == RiskLevel.MEDIUM }
                        "Alto" -> state.evaluations.filter { it.riskLevel == RiskLevel.HIGH }
                        else -> state.evaluations
                    }

                    HistoryStatistics(state.evaluations)

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredEvaluations) { evaluation ->
                            EnhancedHistoryCard(evaluation)
                        }

                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }

                is HistoryViewModel.UiState.Error -> {
                    ErrorHistoryState(
                        message = state.message,
                        onRetry = { viewModel.loadHistory() }
                    )
                }
            }
        }
    }
}

@Composable
fun FilterSection(
    selectedFilter: String,
    onFilterSelected: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "🔍 Filtrar por Risco:",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    label = "Todos",
                    selected = selectedFilter == "Todos",
                    onClick = { onFilterSelected("Todos") },
                    color = Color.Gray
                )
                FilterChip(
                    label = "Baixo",
                    selected = selectedFilter == "Baixo",
                    onClick = { onFilterSelected("Baixo") },
                    color = Color(0xFF10B981)
                )
                FilterChip(
                    label = "Médio",
                    selected = selectedFilter == "Médio",
                    onClick = { onFilterSelected("Médio") },
                    color = Color(0xFFF59E0B)
                )
                FilterChip(
                    label = "Alto",
                    selected = selectedFilter == "Alto",
                    onClick = { onFilterSelected("Alto") },
                    color = Color(0xFFEF4444)
                )
            }
        }
    }
}

@Composable
fun FilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    color: Color
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = if (selected) color else Color.Gray.copy(alpha = 0.2f),
        modifier = Modifier.height(36.dp)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 14.sp
        )
    }
}

@Composable
fun HistoryStatistics(evaluations: List<SafetyEvaluation>) {
    val lowCount = evaluations.count { it.riskLevel == RiskLevel.LOW }
    val mediumCount = evaluations.count { it.riskLevel == RiskLevel.MEDIUM }
    val highCount = evaluations.count { it.riskLevel == RiskLevel.HIGH }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0EA5E9).copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "📊 Estatísticas Gerais",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem("Total", evaluations.size.toString(), Color.Gray)
                StatItem("Baixo", lowCount.toString(), Color(0xFF10B981))
                StatItem("Médio", mediumCount.toString(), Color(0xFFF59E0B))
                StatItem("Alto", highCount.toString(), Color(0xFFEF4444))
            }
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
fun StatItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(color),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun EnhancedHistoryCard(evaluation: SafetyEvaluation) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = evaluation.riskLevel.color.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = evaluation.riskLevel.color
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = evaluation.pilotName,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = formatTimestamp(evaluation.timestamp),
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = evaluation.riskLevel.color
                ) {
                    Text(
                        text = evaluation.riskLevel.displayName.uppercase(),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Divider(modifier = Modifier.padding(vertical = 12.dp))

            // Scores Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ScoreBadge("❤️", evaluation.healthScore)
                ScoreBadge("☁️", evaluation.weatherScore)
                ScoreBadge("✈️", evaluation.aircraftScore)
                ScoreBadge("🎯", evaluation.missionScore)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Score Total:",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "${evaluation.totalScore}/20",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (evaluation.mitigationPlan?.isNotBlank() == true) {
                Divider(modifier = Modifier.padding(vertical = 12.dp))
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        Icons.Default.Assignment,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            "Plano de Mitigação:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            evaluation.mitigationPlan,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ScoreBadge(emoji: String, score: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(emoji, fontSize = 20.sp)
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(getScoreColorForHistory(score)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = score.toString(),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
fun EmptyHistoryState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                Icons.Default.Inbox,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = Color.Gray.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Nenhuma avaliação registrada",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Faça sua primeira avaliação de risco\npara começar o histórico de SMS",
                fontSize = 14.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ErrorHistoryState(
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                Icons.Default.Error,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = Color(0xFFEF4444)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Erro ao carregar histórico",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                message,
                fontSize = 14.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRetry) {
                Icon(Icons.Default.Refresh, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Tentar Novamente")
            }
        }
    }
}

fun formatTimestamp(timestamp: String): String {
    return try {
        val formatter = DateTimeFormatter.ISO_DATE_TIME
        val dateTime = LocalDateTime.parse(timestamp, formatter)
        val displayFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm")
        dateTime.format(displayFormatter)
    } catch (e: Exception) {
        timestamp
    }
}

fun getScoreColorForHistory(score: Int): Color {
    return when (score) {
        1 -> Color(0xFF10B981)
        2 -> Color(0xFF34D399)
        3 -> Color(0xFFF59E0B)
        4 -> Color(0xFFEF4444)
        5 -> Color(0xFFDC2626)
        else -> Color.Gray
    }
}
