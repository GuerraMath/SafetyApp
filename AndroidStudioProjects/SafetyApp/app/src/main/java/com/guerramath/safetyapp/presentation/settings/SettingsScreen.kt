package com.guerramath.safetyapp.presentation.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val NeonPurple = Color(0xFF8B5CF6)
private val BorderInactive = Color.White.copy(alpha = 0.2f)

@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit
) {
    var notificationsEnabled by remember { mutableStateOf(true) }
    var darkModeEnabled by remember { mutableStateOf(true) }
    var biometricEnabled by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF020617), Color(0xFF1E293B))))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            GlassTopBarSettings("CONFIGURAÇÕES", onNavigateBack)

            Text("GERAL", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NeonPurple)

            GlassSettingItem(
                icon = Icons.Default.Notifications,
                title = "Notificações",
                subtitle = "Alertas de segurança e checklist",
                checked = notificationsEnabled,
                onCheckedChange = { notificationsEnabled = it }
            )

            GlassSettingItem(
                icon = Icons.Default.DarkMode,
                title = "Modo Noturno",
                subtitle = "Melhor visualização em voo noturno",
                checked = darkModeEnabled,
                onCheckedChange = { darkModeEnabled = it }
            )

            Text("SEGURANÇA", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NeonPurple)

            GlassSettingItem(
                icon = Icons.Default.Security,
                title = "Biometria",
                subtitle = "Exigir FaceID/TouchID para entrar",
                checked = biometricEnabled,
                onCheckedChange = { biometricEnabled = it }
            )

            Spacer(modifier = Modifier.weight(1f))

            Text("Safety App Build 1.0.25", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.align(Alignment.CenterHorizontally))
        }
    }
}

@Composable
fun GlassSettingItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Surface(
        color = if (checked) NeonPurple.copy(alpha = 0.1f) else Color.White.copy(alpha = 0.05f),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, if (checked) NeonPurple.copy(alpha = 0.5f) else BorderInactive),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = if (checked) NeonPurple else Color.Gray)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = Color.White, fontWeight = FontWeight.Bold)
                Text(subtitle, color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = NeonPurple,
                    checkedTrackColor = NeonPurple.copy(alpha = 0.3f),
                    uncheckedThumbColor = Color.Gray,
                    uncheckedTrackColor = Color.Transparent
                )
            )
        }
    }
}

@Composable
fun GlassTopBarSettings(title: String, onBack: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onBack) {
            Icon(Icons.Default.ArrowBack, null, tint = Color.White)
        }
        Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
    }
}