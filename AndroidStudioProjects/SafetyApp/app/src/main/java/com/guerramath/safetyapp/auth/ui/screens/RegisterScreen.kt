package com.guerramath.safetyapp.auth.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
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
fun RegisterScreen(
    viewModel: AuthViewModel,
    onNavigateBack: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    val authState by viewModel.authState.collectAsState()
    val emailError by viewModel.emailError.collectAsState()
    val passwordError by viewModel.passwordError.collectAsState()
    val nameError by viewModel.nameError.collectAsState()
    val confirmPasswordError by viewModel.confirmPasswordError.collectAsState()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var agreeToTerms by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    // Animação de entrada
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isVisible = true
    }

    // Detectar sucesso no registro
    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onRegisterSuccess()
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
        ) {
            // ═══════════════════════════════════════════════════════════════
            // TOP BAR
            // ═══════════════════════════════════════════════════════════════

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
                Spacer(modifier = Modifier.height(16.dp))

                // ═══════════════════════════════════════════════════════════════
                // CABEÇALHO COM ANIMAÇÃO
                // ═══════════════════════════════════════════════════════════════

                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(500)) + slideInVertically(
                        initialOffsetY = { -50 },
                        animationSpec = tween(500)
                    )
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Ícone
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .background(
                                    brush = AuthColors.GradientPrimary,
                                    shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "✨",
                                fontSize = 36.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "Criar conta",
                            color = AuthColors.TextPrimary,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Preencha os dados abaixo para se registrar",
                            color = AuthColors.TextSecondary,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                // ═══════════════════════════════════════════════════════════════
                // FORMULÁRIO COM ANIMAÇÃO
                // ═══════════════════════════════════════════════════════════════

                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(500, delayMillis = 200)) + slideInVertically(
                        initialOffsetY = { 50 },
                        animationSpec = tween(500, delayMillis = 200)
                    )
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Mensagem de erro
                        if (authState is AuthState.Error) {
                            AuthMessage(
                                message = (authState as AuthState.Error).message,
                                isError = true,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                        }

                        // Campo de Nome
                        AuthTextField(
                            value = name,
                            onValueChange = {
                                name = it
                                viewModel.validateName(it)
                            },
                            label = "Nome completo",
                            leadingIcon = Icons.Outlined.Person,
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next,
                            isError = nameError != null,
                            errorMessage = nameError
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Campo de Email
                        AuthTextField(
                            value = email,
                            onValueChange = {
                                email = it
                                viewModel.validateEmail(it)
                            },
                            label = "Email",
                            leadingIcon = Icons.Outlined.Email,
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next,
                            isError = emailError != null,
                            errorMessage = emailError
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Campo de Senha
                        AuthTextField(
                            value = password,
                            onValueChange = {
                                password = it
                                viewModel.validatePassword(it)
                            },
                            label = "Senha",
                            leadingIcon = Icons.Outlined.Lock,
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Next,
                            isError = passwordError != null,
                            errorMessage = passwordError,
                            isPassword = true
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Campo de Confirmação de Senha
                        AuthTextField(
                            value = confirmPassword,
                            onValueChange = {
                                confirmPassword = it
                                viewModel.validateConfirmPassword(password, it)
                            },
                            label = "Confirmar senha",
                            leadingIcon = Icons.Outlined.Lock,
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done,
                            isError = confirmPasswordError != null,
                            errorMessage = confirmPasswordError,
                            isPassword = true,
                            onImeAction = {
                                focusManager.clearFocus()
                            }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Aceitar Termos
                        Row(
                            modifier = Modifier.clickable {
                                agreeToTerms = !agreeToTerms
                            },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = agreeToTerms,
                                onCheckedChange = { agreeToTerms = it },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = AuthColors.Primary,
                                    uncheckedColor = AuthColors.Border
                                )
                            )
                            Text(
                                text = "Concordo com os Termos de Serviço",
                                color = AuthColors.TextSecondary,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // Botão de Registro
                        AuthPrimaryButton(
                            text = "Criar conta",
                            onClick = {
                                focusManager.clearFocus()
                                viewModel.register(email, password, name, confirmPassword)
                            },
                            enabled = name.isNotEmpty() && email.isNotEmpty() &&
                                    password.isNotEmpty() && confirmPassword.isNotEmpty() &&
                                    agreeToTerms &&
                                    nameError == null && emailError == null &&
                                    passwordError == null && confirmPasswordError == null,
                            isLoading = authState is AuthState.Loading
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Já tem conta?
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Já tem uma conta? ",
                                color = AuthColors.TextSecondary,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Fazer login",
                                color = AuthColors.Primary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.clickable {
                                    onNavigateBack()
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
    }
}