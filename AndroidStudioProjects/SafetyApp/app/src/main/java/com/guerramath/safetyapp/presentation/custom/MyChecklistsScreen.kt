package com.guerramath.safetyapp.presentation.custom

import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.guerramath.safetyapp.domain.model.CustomChecklist
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MyChecklistsScreen(
    viewModel: CustomChecklistViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToCreate: () -> Unit,
    onNavigateToEdit: (String) -> Unit
) {
    val checklists by viewModel.savedChecklists.collectAsState(initial = emptyList())
    var showDeleteDialog by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "MEUS CHECKLISTS",
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp
                        )
                        Text(
                            "Personalizados",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Voltar", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0EA5E9)
                )
            )
        },
        floatingActionButton = {
            NeoBrutalistFAB(
                onClick = {
                    viewModel.createNewChecklist("")
                    onNavigateToCreate()
                },
                icon = Icons.Default.Add,
                backgroundColor = Color(0xFF10B981),
                shadowColor = Color(0xFF059669)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
        ) {
            if (checklists.isEmpty()) {
                EmptyChecklistsState()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Stats Card
                    item {
                        StatsCard(checklistCount = checklists.size)
                    }

                    // Checklists
                    items(
                        items = checklists,
                        key = { it.id }
                    ) { checklist ->
                        ChecklistCard(
                            modifier = Modifier.animateItemPlacement(),
                            checklist = checklist,
                            onClick = { onNavigateToEdit(checklist.id) },
                            onDelete = { showDeleteDialog = checklist.id }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }

    // Delete Confirmation Dialog
    showDeleteDialog?.let { checklistId ->
        DeleteConfirmationDialog(
            onDismiss = { showDeleteDialog = null },
            onConfirm = {
                viewModel.deleteChecklist(checklistId)
                showDeleteDialog = null
            }
        )
    }
}

@Composable
fun StatsCard(checklistCount: Int) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = Color(0xFF0EA5E9).copy(alpha = 0.1f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "TOTAL DE CHECKLISTS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Criados por você",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(Color(0xFF0EA5E9), CircleShape)
                    .border(3.dp, Color.Black, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    checklistCount.toString(),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun ChecklistCard(
    modifier: Modifier = Modifier,
    checklist: CustomChecklist,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    GlassCard(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color(0xFF0EA5E9).copy(alpha = 0.2f), CircleShape)
                            .border(2.dp, Color.Black, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Assignment,
                            contentDescription = null,
                            tint = Color(0xFF0EA5E9),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            checklist.title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            formatDate(checklist.updatedAt),
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }

                // Menu Button
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "Menu",
                            tint = Color.Black
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Editar", fontWeight = FontWeight.Bold) },
                            onClick = {
                                showMenu = false
                                onClick()
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Edit, null)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Deletar", fontWeight = FontWeight.Bold, color = Color(0xFFEF4444)) },
                            onClick = {
                                showMenu = false
                                onDelete()
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Delete, null, tint = Color(0xFFEF4444))
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatBadge(
                    icon = Icons.Default.ChecklistRtl,
                    value = "${checklist.items.size}",
                    label = "Itens",
                    color = Color(0xFF0EA5E9)
                )

                val completedCount = checklist.items.count { it.isChecked }
                StatBadge(
                    icon = Icons.Default.CheckCircle,
                    value = "$completedCount",
                    label = "Concluídos",
                    color = Color(0xFF10B981)
                )

                val progress = if (checklist.items.isNotEmpty()) {
                    (completedCount.toFloat() / checklist.items.size * 100).toInt()
                } else 0

                StatBadge(
                    icon = Icons.Default.Percent,
                    value = "$progress%",
                    label = "Progresso",
                    color = Color(0xFFF59E0B)
                )
            }
        }
    }
}

@Composable
fun StatBadge(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    color: Color
) {
    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
            .border(2.dp, Color.Black, RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(16.dp)
            )
            Text(
                value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                color = color
            )
            Text(
                label,
                fontSize = 11.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun EmptyChecklistsState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                Icons.Default.PlaylistAdd,
                contentDescription = null,
                modifier = Modifier.size(120.dp),
                tint = Color.Gray.copy(alpha = 0.3f)
            )
            Text(
                "NENHUM CHECKLIST",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = Color.Gray
            )
            Text(
                "Crie seu primeiro checklist\npersonalizado clicando no botão +",
                fontSize = 16.sp,
                color = Color.Gray.copy(alpha = 0.7f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
fun DeleteConfirmationDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                Icons.Default.Warning,
                contentDescription = null,
                tint = Color(0xFFEF4444),
                modifier = Modifier.size(48.dp)
            )
        },
        title = {
            Text(
                "DELETAR CHECKLIST?",
                fontWeight = FontWeight.Black,
                fontSize = 18.sp
            )
        },
        text = {
            Text(
                "Esta ação não pode ser desfeita. O checklist será permanentemente removido.",
                fontSize = 14.sp
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFEF4444)
                )
            ) {
                Text("DELETAR", fontWeight = FontWeight.Black)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("CANCELAR", fontWeight = FontWeight.Bold)
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp)
    )
}

fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}