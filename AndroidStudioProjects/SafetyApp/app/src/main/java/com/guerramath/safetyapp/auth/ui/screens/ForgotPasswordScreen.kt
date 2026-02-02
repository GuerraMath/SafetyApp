package com.guerramath.safetyapp.auth.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.MarkEmailRead
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.guerramath.safetyapp.auth.ui.components.*
import com.guerramath.safetyapp.auth.viewmodel.AuthState
import com.guerramath.safetyapp.auth.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    viewModel: AuthViewModel,
    onNavigateBack: () -> Unit
) {
    val authState by viewModel.authState.collectAsState()
    val emailError by viewModel.emailError.collectAsState()

    var email by remember { mutableStateOf("") }
    var emailSent by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isVisible = true
    }

    // Detectar sucesso no envio
    LaunchedEffect(authState) {
        // CORREÇÃO: Usar o nome correto do estado definido no ViewModel
        if (authState is AuthState.ForgotPasswordSent) {
            emailSent = true
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
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {

            // --- TOP BAR ---
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = AuthColors.TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                AnimatedContent(
                    targetState = emailSent,
                    transitionSpec = {
                        fadeIn(tween(300)) togetherWith fadeOut(tween(300))
                    },
                    label = "emailSentTransition"
                ) { sent ->
                    if (sent) {
                        // --- TELA DE SUCESSO ---
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .background(
                                        color = AuthColors.Success.copy(alpha = 0.1f),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.MarkEmailRead,
                                    contentDescription = null,
                                    tint = AuthColors.Success,
                                    modifier = Modifier.size(48.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(32.dp))

                            Text(
                                text = "Email enviado!",
                                color = AuthColors.TextPrimary,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Enviamos um link de recuperação para:\n$email",
                                color = AuthColors.TextSecondary,
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center,
                                lineHeight = 24.sp
                            )

                            Spacer(modifier = Modifier.height(48.dp))

                            AuthPrimaryButton(
                                text = "Voltar ao login",
                                onClick = onNavigateBack
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            AuthTextButton(
                                text = "Não recebeu? Reenviar email",
                                onClick = {
                                    emailSent = false
                                    viewModel.resetState()
                                }
                            )
                        }
                    } else {
                        // --- FORMULÁRIO ---
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            AnimatedVisibility(
                                visible = isVisible,
                                enter = fadeIn(tween(500)) + slideInVertically(initialOffsetY = { -50 })
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(
                                        modifier = Modifier
                                            .size(80.dp)
                                            .background(
                                                brush = AuthColors.GradientPrimary,
                                                shape = RoundedCornerShape(20.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = "🔑", fontSize = 36.sp)
                                    }

                                    Spacer(modifier = Modifier.height(24.dp))

                                    Text(
                                        text = "Esqueceu a senha?",
                                        color = AuthColors.TextPrimary,
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = "Não se preocupe! Digite seu email e enviaremos instruções.",
                                        color = AuthColors.TextSecondary,
                                        fontSize = 16.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(48.dp))

                            AnimatedVisibility(
                                visible = isVisible,
                                enter = fadeIn(tween(500, delayMillis = 200)) + slideInVertically(initialOffsetY = { 50 })
                            ) {
                                Column {
                                    if (authState is AuthState.Error) {
                                        AuthMessage(
                                            message = (authState as AuthState.Error).message,
                                            isError = true,
                                            modifier = Modifier.padding(bottom = 16.dp)
                                        )
                                    }

                                    AuthTextField(
                                        value = email,
                                        onValueChange = {
                                            email = it
                                            viewModel.validateEmail(it)
                                        },
                                        label = "Email",
                                        leadingIcon = Icons.Outlined.Email,
                                        keyboardType = KeyboardType.Email,
                                        imeAction = ImeAction.Done,
                                        isError = emailError != null,
                                        errorMessage = emailError,
                                        onImeAction = {
                                            focusManager.clearFocus()
                                            if (emailError == null && email.isNotEmpty()) {
                                                viewModel.forgotPassword(email)
                                            }
                                        }
                                    )

                                    Spacer(modifier = Modifier.height(32.dp))

                                    AuthPrimaryButton(
                                        text = "Enviar link de recuperação",
                                        onClick = {
                                            focusManager.clearFocus()
                                            viewModel.forgotPassword(email)
                                        },
                                        enabled = email.isNotEmpty() && emailError == null,
                                        isLoading = authState is AuthState.Loading
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Box(
                                        modifier = Modifier.fillMaxWidth(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        AuthTextButton(
                                            text = "Voltar ao login",
                                            onClick = onNavigateBack,
                                            color = AuthColors.TextSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}