package com.guerramath.safetyapp.presentation.evaluation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.guerramath.safetyapp.domain.model.ChecklistCategory
import com.guerramath.safetyapp.domain.model.ChecklistItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChecklistScreen(
    viewModel: ChecklistViewModel,
    onOpenDrawer: () -> Unit,
    onNavigateToHistory: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val categories by viewModel.categories.collectAsState()

    var pilotName by remember { mutableStateOf("") }
    var mitigationPlan by remember { mutableStateOf("") }

    // Calcular progresso
    val totalItems = categories.sumOf { it.items.size }
    val checkedItems = categories.sumOf { category -> category.items.count { it.isChecked } }
    val progress = if (totalItems > 0) checkedItems.toFloat() / totalItems else 0f

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "SMS Dashboard",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            "Safety Management System | M.Sc. ITA",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(
                            Icons.Default.Menu,
                            contentDescription = "Menu",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToHistory) {
                        Icon(
                            Icons.Default.History,
                            "Histórico",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0EA5E9)
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // API Status Card
            ApiStatusCard()

            // Pilot Info
            PilotInfoCard(
                pilotName = pilotName,
                onPilotNameChange = { pilotName = it }
            )

            // Progress Card
            ProgressCard(
                checkedItems = checkedItems,
                totalItems = totalItems,
                progress = progress
            )

            // Title
            Text(
                text = "📋 1. Check-list de Prontidão (20 Itens)",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            // Categories
            categories.forEach { category ->
                ChecklistCategoryCard(
                    category = category,
                    onCheckChanged = { itemId, isChecked ->
                        viewModel.updateChecklistItem(category.id, itemId, isChecked)
                    },
                    onCommentChanged = { itemId, comment ->
                        viewModel.updateComment(category.id, itemId, comment)
                    }
                )
            }

            // Mitigation Plan
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "📝 Plano de Mitigação",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = mitigationPlan,
                        onValueChange = { mitigationPlan = it },
                        label = { Text("Ações e Observações Gerais") },
                        placeholder = { Text("Ex: Aguardar melhoria das condições, briefing reforçado...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 5
                    )
                }
            }

            // Submit Button
            Button(
                onClick = {
                    if (pilotName.isNotBlank()) {
                        viewModel.submitEvaluation(pilotName, mitigationPlan)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = pilotName.isNotBlank() && uiState !is ChecklistViewModel.UiState.Loading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF10B981)
                )
            ) {
                if (uiState is ChecklistViewModel.UiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Enviando...")
                } else {
                    Icon(Icons.Default.Send, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Enviar Avaliação", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Result Display
            AnimatedVisibility(visible = uiState is ChecklistViewModel.UiState.Success) {
                when (val state = uiState) {
                    is ChecklistViewModel.UiState.Success -> {
                        ResultCard(
                            evaluation = state.evaluation,
                            onDismiss = { viewModel.resetState() }
                        )
                    }
                    else -> {}
                }
            }

            AnimatedVisibility(visible = uiState is ChecklistViewModel.UiState.Error) {
                when (val state = uiState) {
                    is ChecklistViewModel.UiState.Error -> {
                        ErrorCard(
                            message = state.message,
                            onDismiss = { viewModel.resetState() }
                        )
                    }
                    else -> {}
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun ApiStatusCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF10B981).copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(Color(0xFF10B981), shape = androidx.compose.foundation.shape.CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "API STATUS: ONLINE",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF10B981)
            )
        }
    }
}

@Composable
fun PilotInfoCard(
    pilotName: String,
    onPilotNameChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "👨‍✈️ Dados do Piloto",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = pilotName,
                onValueChange = onPilotNameChange,
                label = { Text("Nome Completo") },
                placeholder = { Text("Ex: Matheus Guerra") },
                leadingIcon = { Icon(Icons.Default.Person, null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }
    }
}

@Composable
fun ProgressCard(
    checkedItems: Int,
    totalItems: Int,
    progress: Float
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0EA5E9).copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Progresso do Checklist",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "$checkedItems / $totalItems",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0EA5E9)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = Color(0xFF0EA5E9),
                trackColor = Color.Gray.copy(alpha = 0.2f)
            )
        }
    }
}

@Composable
fun ChecklistCategoryCard(
    category: ChecklistCategory,
    onCheckChanged: (String, Boolean) -> Unit,
    onCommentChanged: (String, String) -> Unit
) {
    var expandedItems by remember { mutableStateOf(setOf<String>()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Category Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Text(
                    category.emoji,
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        category.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        category.description,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Divider()

            Spacer(modifier = Modifier.height(12.dp))

            // Items
            category.items.forEach { item ->
                ChecklistItemRow(
                    item = item,
                    isExpanded = expandedItems.contains(item.id),
                    onCheckChanged = { onCheckChanged(item.id, it) },
                    onToggleExpand = {
                        expandedItems = if (expandedItems.contains(item.id)) {
                            expandedItems - item.id
                        } else {
                            expandedItems + item.id
                        }
                    },
                    onCommentChanged = { onCommentChanged(item.id, it) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun ChecklistItemRow(
    item: ChecklistItem,
    isExpanded: Boolean,
    onCheckChanged: (Boolean) -> Unit,
    onToggleExpand: () -> Unit,
    onCommentChanged: (String) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = item.isChecked,
                onCheckedChange = onCheckChanged,
                colors = CheckboxDefaults.colors(
                    checkedColor = Color(0xFF10B981)
                )
            )

            Text(
                text = item.text,
                fontSize = 14.sp,
                modifier = Modifier.weight(1f)
            )

            IconButton(onClick = onToggleExpand) {
                Icon(
                    if (isExpanded) Icons.Default.ExpandLess else Icons.Default.Comment,
                    contentDescription = "Comentário",
                    tint = if (item.comment.isNotBlank()) Color(0xFF0EA5E9)
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        AnimatedVisibility(visible = isExpanded) {
            OutlinedTextField(
                value = item.comment,
                onValueChange = onCommentChanged,
                label = { Text("Comentário/Observação", fontSize = 12.sp) },
                placeholder = { Text("Adicione detalhes se necessário...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 48.dp, top = 4.dp, bottom = 8.dp),
                minLines = 2,
                maxLines = 3,
                textStyle = MaterialTheme.typography.bodySmall
            )
        }
    }
}