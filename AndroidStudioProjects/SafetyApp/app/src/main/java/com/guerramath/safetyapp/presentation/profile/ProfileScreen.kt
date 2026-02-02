package com.guerramath.safetyapp.presentation.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Reutilizando cores do tema Glass
private val NeonCyan = Color(0xFF06B6D4)
private val GlassDark = Color(0xFF0F172A)
private val BorderInactive = Color.White.copy(alpha = 0.2f)

@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit // Adicionado o parâmetro onLogout
) {
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
            // Header com Botão Voltar
            GlassTopBar("MEU PERFIL", onNavigateBack)

            // Cartão do Usuário
            GlassCard {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(NeonCyan, Color.Blue))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(50.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Matheus Guerra", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("M.Sc. ITA | Piloto & Instrutor", fontSize = 14.sp, color = NeonCyan)
                }
            }

            // Estatísticas (Grid 2x2 simulado)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                GlassStatCard("VOOS", "1,240h", Modifier.weight(1f))
                GlassStatCard("SMS SCORE", "98%", Modifier.weight(1f))
            }

            // Detalhes
            GlassCard {
                Column(modifier = Modifier.padding(16.dp)) {
                    ProfileRow(Icons.Default.Email, "Email", "ten.matheus.guerra@gmail.com")
                    Spacer(modifier = Modifier.height(16.dp))
                    ProfileRow(Icons.Default.Phone, "Telefone", "(71) 99196-1010")
                    Spacer(modifier = Modifier.height(16.dp))
                    ProfileRow(Icons.Default.Badge, "Licença", "PC/IFI - ANAC 123456")
                }
            }

            // Botão de Logout
            Spacer(modifier = Modifier.height(24.dp)) // Espaçamento antes do botão de logout
            Button(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Logout", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun GlassCard(content: @Composable () -> Unit) {
    Surface(
        color = Color.White.copy(alpha = 0.05f),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, BorderInactive),
        modifier = Modifier.fillMaxWidth()
    ) {
        content()
    }
}

@Composable
fun GlassStatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        color = NeonCyan.copy(alpha = 0.1f),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.3f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

@Composable
fun ProfileRow(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = Color.White.copy(alpha = 0.7f), modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(label, fontSize = 10.sp, color = Color.White.copy(alpha = 0.5f))
            Text(value, fontSize = 14.sp, color = Color.White)
        }
    }
}

@Composable
fun GlassTopBar(title: String, onBack: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onBack) {
            Icon(Icons.Default.ArrowBack, null, tint = Color.White)
        }
        Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
    }
}
