package com.guerramath.safetyapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.guerramath.safetyapp.auth.data.preferences.AuthPreferences
// import com.guerramath.safetyapp.auth.data.preferences.UserPreferences // REMOVER: Não precisamos mais deste
import com.guerramath.safetyapp.auth.ui.components.AuthColors
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Usamos apenas AuthPreferences agora
    val authPreferences = remember { AuthPreferences(context) }

    // Coletar dados diretamente do Flow do AuthPreferences
    val userName by authPreferences.userName.collectAsState(initial = "Usuário")
    val userEmail by authPreferences.userEmail.collectAsState(initial = "")

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home_main"
    ) {
        composable("home_main") {
            HomeMainContent(
                userName = userName ?: "Usuário",
                userEmail = userEmail ?: "",
                onSettingsClick = {
                    navController.navigate("settings")
                },
                onLogout = {
                    scope.launch {
                        // CORREÇÃO: Nome do método atualizado
                        authPreferences.clearAuthData()
                        onLogout()
                    }
                }
            )
        }

        composable("settings") {
            // Placeholder simples para SettingsScreen se ela não existir ainda neste contexto
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Button(onClick = {
                    scope.launch {
                        authPreferences.clearAuthData()
                        onLogout()
                    }
                }) {
                    Text("Sair do App")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeMainContent(
    userName: String,
    userEmail: String,
    onSettingsClick: () -> Unit,
    onLogout: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        AuthColors.Background,
                        Color(0xFF1A1A2E)
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(AuthColors.GradientPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = userName.firstOrNull()?.uppercase() ?: "U",
                                color = AuthColors.TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Olá, $userName!",
                                color = AuthColors.TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = userEmail,
                                color = AuthColors.TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Configurações",
                            tint = AuthColors.TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )

            // Resto do layout igual...
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "🎉", fontSize = 64.sp)
                Text(text = "Logado com sucesso!", color = Color.White)
            }
        }
    }
}