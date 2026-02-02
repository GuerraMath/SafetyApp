
package com.guerramath.safetyapp.auth.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import com.guerramath.safetyapp.auth.biometric.BiometricAuthManager
import com.guerramath.safetyapp.auth.biometric.BiometricResult
import com.guerramath.safetyapp.auth.ui.components.AuthColors
import com.guerramath.safetyapp.auth.ui.components.AuthPrimaryButton
import com.guerramath.safetyapp.auth.ui.components.AuthTextButton

/**
 * Tela de login rápido com biometria.
 * Mostrada quando usuário tem sessão salva e biometria habilitada.
 */
@Composable
fun BiometricLoginScreen(
    userName: String,
    userEmail: String,
    onBiometricSuccess: () -> Unit,
    onUsePassword: () -> Unit,
    onSwitchAccount: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? FragmentActivity

    val biometricManager = remember { BiometricAuthManager(context) }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isAuthenticating by remember { mutableStateOf(false) }

    // Animação do ícone
    val infiniteTransition = rememberInfiniteTransition(label = "biometric")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    // Função para iniciar autenticação
    fun startBiometricAuth() {
        if (activity == null) {
            errorMessage = "Erro ao iniciar autenticação"
            return
        }

        isAuthenticating = true
        errorMessage = null

        biometricManager.authenticate(
            activity = activity,
            title = "Entrar no SafetyApp",
            subtitle = "Use sua biometria para acessar",
            negativeButtonText = "Usar senha",
            onResult = { result ->
                isAuthenticating = false

                when (result) {
                    is BiometricResult.Success -> {
                        onBiometricSuccess()
                    }
                    is BiometricResult.Cancelled -> {
                        // Usuário cancelou - não faz nada
                    }
                    is BiometricResult.Error -> {
                        errorMessage = result.message
                    }
                    is BiometricResult.NotAvailable -> {
                        errorMessage = "Biometria não disponível"
                        onUsePassword()
                    }
                    is BiometricResult.NotEnrolled -> {
                        errorMessage = "Configure sua biometria nas configurações do dispositivo"
                    }
                }
            }
        )
    }

    // Inicia autenticação automaticamente
    LaunchedEffect(Unit) {
        if (biometricManager.isBiometricAvailable()) {
            startBiometricAuth()
        }
    }

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
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(80.dp))

            // Avatar do usuário
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(AuthColors.GradientPrimary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = userName.firstOrNull()?.uppercase() ?: "U",
                    color = AuthColors.TextPrimary,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Nome do usuário
            Text(
                text = "Olá, $userName!",
                color = AuthColors.TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Email
            Text(
                text = userEmail,
                color = AuthColors.TextSecondary,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Ícone de biometria (clicável)
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .scale(scale)
                    .clip(CircleShape)
                    .background(AuthColors.Surface)
                    .clickable(enabled = !isAuthenticating) { startBiometricAuth() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Fingerprint,
                    contentDescription = "Autenticação biométrica",
                    tint = AuthColors.Primary,
                    modifier = Modifier.size(56.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (isAuthenticating) "Autenticando..." else "Toque para usar biometria",
                color = AuthColors.TextSecondary,
                fontSize = 14.sp
            )

            // Mensagem de erro
            AnimatedVisibility(
                visible = errorMessage != null,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                Text(
                    text = errorMessage ?: "",
                    color = AuthColors.Error,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 16.dp, start = 32.dp, end = 32.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Botão para usar senha
            AuthPrimaryButton(
                text = "Usar senha",
                onClick = onUsePassword
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Link para trocar conta
            AuthTextButton(
                text = "Usar outra conta",
                onClick = onSwitchAccount,
                color = AuthColors.TextSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

