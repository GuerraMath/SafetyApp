package com.guerramath.safetyapp.presentation.custom

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.guerramath.safetyapp.domain.model.CustomChecklistItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateChecklistScreen(
    viewModel: CustomChecklistViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToList: () -> Unit
) {
    val currentChecklist by viewModel.currentChecklist.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }

    LaunchedEffect(currentChecklist) {
        title = currentChecklist?.title ?: ""
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Criar Checklist", fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Voltar")
                    }
                },
                actions = {
                    // Botão para ver checklists salvos
                    IconButton(onClick = onNavigateToList) {
                        Icon(Icons.Default.List, "Meus Checklists")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0EA5E9)
                )
            )
        },
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // FAB Salvar (Diskette)
                NeoBrutalistFAB(
                    onClick = {
                        viewModel.updateTitle(title)
                        viewModel.saveChecklist()
                        onNavigateToList()
                    },
                    icon = Icons.Default.Save,
                    backgroundColor = Color(0xFF10B981),
                    shadowColor = Color(0xFF059669)
                )

                // FAB Adicionar Item
                NeoBrutalistFAB(
                    onClick = { showAddDialog = true },
                    icon = Icons.Default.Add,
                    backgroundColor = Color(0xFF0EA5E9),
                    shadowColor = Color(0xFF0284C7)
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Title Card (Neo-Brutalist + Glassmorphism)
            GlassCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        "📋 TÍTULO DO CHECKLIST",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        placeholder = { Text("Ex: Checklist de Decolagem") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF0EA5E9),
                            unfocusedBorderColor = Color.Black
                        )
                    )
                }
            }

            // Items Count
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = Color(0xFF0EA5E9).copy(alpha = 0.1f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "ITENS ADICIONADOS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        "${currentChecklist?.items?.size ?: 0}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF0EA5E9)
                    )
                }
            }

            // Items List
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                currentChecklist?.items?.let { items ->
                    items(items) { item ->
                        ChecklistItemCard(
                            item = item,
                            onToggle = { viewModel.toggleItem(item.id) },
                            onDelete = { viewModel.removeItem(item.id) }
                        )
                    }
                }

                if (currentChecklist?.items.isNullOrEmpty()) {
                    item {
                        EmptyState()
                    }
                }
            }
        }
    }

    // Add Item Dialog
    if (showAddDialog) {
        AddItemDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { text ->
                viewModel.addItem(text)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun NeoBrutalistFAB(
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    backgroundColor: Color,
    shadowColor: Color
) {
    Box(
        modifier = Modifier
            .size(64.dp)
            .offset(x = (-4).dp, y = (-4).dp)
            .background(shadowColor, CircleShape)
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .offset(x = 4.dp, y = 4.dp)
                .clip(CircleShape)
                .background(backgroundColor)
                .border(3.dp, Color.Black, CircleShape)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = Color.White
            )
        }
    }
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.White,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .offset(x = (-4).dp, y = (-4).dp)
            .background(Color.Black, RoundedCornerShape(16.dp))
    ) {
        Box(
            modifier = Modifier
                .offset(x = 4.dp, y = 4.dp)
                .background(backgroundColor, RoundedCornerShape(16.dp))
                .border(3.dp, Color.Black, RoundedCornerShape(16.dp))
        ) {
            content()
        }
    }
}

@Composable
fun ChecklistItemCard(
    item: CustomChecklistItem,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = if (item.isChecked)
            Color(0xFF10B981).copy(alpha = 0.2f)
        else
            Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(
                        if (item.isChecked) Color(0xFF10B981) else Color.White,
                        RoundedCornerShape(6.dp)
                    )
                    .border(3.dp, Color.Black, RoundedCornerShape(6.dp))
                    .clickable(onClick = onToggle),
                contentAlignment = Alignment.Center
            ) {
                if (item.isChecked) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = item.text,
                fontSize = 16.sp,
                modifier = Modifier.weight(1f),
                fontWeight = if (item.isChecked) FontWeight.Normal else FontWeight.Bold
            )

            // Delete button
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Remover",
                    tint = Color(0xFFEF4444)
                )
            }
        }
    }
}

@Composable
fun EmptyState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                Icons.Default.AddCircleOutline,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = Color.Gray.copy(alpha = 0.3f)
            )
            Text(
                "Nenhum item adicionado",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
            Text(
                "Clique no botão + para adicionar\nitens ao seu checklist",
                fontSize = 14.sp,
                color = Color.Gray.copy(alpha = 0.7f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
fun AddItemDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "ADICIONAR ITEM",
                fontWeight = FontWeight.Black,
                fontSize = 18.sp
            )
        },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Texto do Item") },
                placeholder = { Text("Ex: Verificar combustível") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    if (text.isNotBlank()) {
                        onConfirm(text)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF10B981)
                )
            ) {
                Text("ADICIONAR", fontWeight = FontWeight.Black)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCELAR", fontWeight = FontWeight.Bold)
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp)
    )
}