package com.guerramath.safetyapp.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.guerramath.safetyapp.auth.ui.components.AuthColors
import kotlinx.coroutines.delay

/**
 * Tela de Splash com animação.
 * Verifica sessão e redireciona para Login ou Home.
 */
@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit,
    checkSession: suspend () -> Boolean
) {
    var isLoading by remember { mutableStateOf(true) }

    // Animações
    val infiniteTransition = rememberInfiniteTransition(label = "splash")

    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val alpha by animateFloatAsState(
        targetValue = if (isLoading) 1f else 0f,
        animationSpec = tween(500),
        label = "alpha"
    )

    // Verificação de sessão
    LaunchedEffect(Unit) {
        // Delay mínimo para mostrar splash (UX)
        delay(1500)

        val isLoggedIn = checkSession()
        isLoading = false

        // Pequeno delay para animação de fade out
        delay(300)

        if (isLoggedIn) {
            onNavigateToHome()
        } else {
            onNavigateToLogin()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        AuthColors.Background,
                        Color(0xFF1A1A2E),
                        Color(0xFF16213E)
                    )
                )
            )
            .alpha(alpha),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo animado
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .scale(scale)
                    .background(
                        brush = AuthColors.GradientPrimary,
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(28.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🛡️",
                    fontSize = 56.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Nome do App
            Text(
                text = "SafetyApp",
                color = AuthColors.TextPrimary,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Sua segurança em primeiro lugar",
                color = AuthColors.TextSecondary,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Loading indicator
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp),
                    color = AuthColors.Primary,
                    strokeWidth = 3.dp
                )
            }
        }

        // Versão do app no rodapé
        Text(
            text = "v1.0.0",
            color = AuthColors.TextSecondary.copy(alpha = 0.5f),
            fontSize = 12.sp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        )
    }
}

