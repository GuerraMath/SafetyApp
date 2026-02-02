package com.guerramath.safetyapp.presentation.navigation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Cores do Tema "Glass-Brutalism"
private val NeonCyan = Color(0xFF06B6D4) // Ciano Reativo
private val NeonGreen = Color(0xFF10B981) // Verde Reativo
private val BorderInactive = Color.White.copy(alpha = 0.2f)

@Composable
fun DrawerContent(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    onCloseDrawer: () -> Unit,
    isLoggedIn: Boolean = false,
    userName: String? = null,
    userEmail: String? = null,
    onLoginClick: (() -> Unit)? = null,
    onLogout: (() -> Unit)? = null
) {
    // Fundo Geral
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF020617), Color(0xFF1E293B))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- HEADER ---
            GlassProfileHeader(
                isLoggedIn = isLoggedIn,
                userName = userName,
                userEmail = userEmail,
                onLoginClick = {
                    onLoginClick?.invoke()
                    onCloseDrawer()
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- MENU ITEMS ---
            SectionTitle("OPERAÇÕES")

            GlassMenuItem(
                icon = Icons.Default.Dashboard,
                label = "Dashboard SMS",
                isSelected = currentRoute == "checklist",
                accentColor = NeonCyan,
                onClick = {
                    onNavigate("checklist")
                    onCloseDrawer()
                }
            )

            GlassMenuItem(
                icon = Icons.AutoMirrored.Filled.ListAlt,
                label = "Meus Checklists",
                isSelected = currentRoute == "my_checklists" || currentRoute == "create_checklist",
                accentColor = NeonGreen,
                onClick = {
                    onNavigate("my_checklists")
                    onCloseDrawer()
                }
            )

            Spacer(modifier = Modifier.height(8.dp))
            SectionTitle("DADOS")

            GlassMenuItem(
                icon = Icons.Default.History,
                label = "Histórico",
                isSelected = currentRoute == "history",
                accentColor = Color(0xFFF59E0B),
                onClick = {
                    onNavigate("history")
                    onCloseDrawer()
                }
            )

            Spacer(modifier = Modifier.height(8.dp))
            SectionTitle("SISTEMA")

            GlassMenuItem(
                icon = Icons.Default.Settings,
                label = "Configurações",
                isSelected = currentRoute == "settings",
                accentColor = Color(0xFF6366F1),
                onClick = {
                    onNavigate("settings")
                    onCloseDrawer()
                }
            )

            Spacer(modifier = Modifier.weight(1f))

            // --- FOOTER (só mostra logout se estiver logado) ---
            if (isLoggedIn && onLogout != null) {
                GlassFooter(onLogout = onLogout)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlassProfileHeader(
    isLoggedIn: Boolean,
    userName: String?,
    userEmail: String?,
    onLoginClick: () -> Unit
) {
    Surface(
        onClick = if (!isLoggedIn) onLoginClick else { {} },
        color = Color.White.copy(alpha = 0.05f),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, if (!isLoggedIn) NeonCyan else BorderInactive),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(
                        if (isLoggedIn) {
                            Brush.linearGradient(listOf(NeonGreen, Color(0xFF059669)))
                        } else {
                            Brush.linearGradient(listOf(NeonCyan, Color.Blue))
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isLoggedIn && !userName.isNullOrBlank()) {
                    Text(
                        text = userName.first().uppercase(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                } else {
                    Icon(
                        imageVector = if (isLoggedIn) Icons.Default.Person else Icons.AutoMirrored.Filled.Login,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                if (isLoggedIn) {
                    Text(
                        text = userName ?: "Usuário",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    if (!userEmail.isNullOrBlank()) {
                        Text(
                            text = userEmail,
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 12.sp
                        )
                    }
                } else {
                    Text(
                        text = "Fazer login",
                        color = NeonCyan,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Toque para entrar na sua conta",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 12.sp
                    )
                }
            }

            if (!isLoggedIn) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = NeonCyan,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlassMenuItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    accentColor: Color,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) accentColor.copy(alpha = 0.15f) else Color.Transparent
    val contentColor = if (isSelected) accentColor else Color.White.copy(alpha = 0.8f)

    val borderBrush = if (isSelected) {
        Brush.horizontalGradient(listOf(accentColor, accentColor.copy(alpha = 0.5f)))
    } else {
        Brush.horizontalGradient(listOf(BorderInactive, BorderInactive))
    }

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                brush = borderBrush,
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = label,
                color = contentColor,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 14.sp,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.weight(1f))

            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(accentColor, CircleShape)
                )
            }
        }
    }
}

@Composable
fun SectionTitle(text: String) {
    Text(
        text = text,
        color = Color.White.copy(alpha = 0.4f),
        fontSize = 11.sp,
        fontWeight = FontWeight.Black,
        letterSpacing = 2.sp,
        modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlassFooter(onLogout: () -> Unit) {
    Surface(
        onClick = onLogout,
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFEF4444).copy(alpha = 0.1f),
        border = BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Logout,
                contentDescription = null,
                tint = Color(0xFFEF4444),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "SAIR DA CONTA",
                color = Color(0xFFEF4444),
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
    }
}
