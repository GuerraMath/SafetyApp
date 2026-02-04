package com.guerramath.safetyapp.auth.ui.screens

import android.app.Activity
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.NoAccounts // Ícone para offline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.guerramath.safetyapp.auth.data.google.GoogleAuthManager
import com.guerramath.safetyapp.auth.data.google.GoogleSignInResult
import com.guerramath.safetyapp.auth.ui.components.*
import com.guerramath.safetyapp.auth.viewmodel.AuthState
import com.guerramath.safetyapp.auth.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    onLoginSuccess: () -> Unit,
    onSkipLogin: () -> Unit // NOVO PARÂMETRO
) {
    val authState by viewModel.authState.collectAsState()
    val emailError by viewModel.emailError.collectAsState()
    val passwordError by viewModel.passwordError.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val activity = context as? Activity
    val coroutineScope = rememberCoroutineScope()

    // Google Auth Manager
    val googleAuthManager = remember { GoogleAuthManager(context) }

    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isVisible = true
    }

    // Função para iniciar login com Google
    fun startGoogleSignIn() {
        val currentActivity = activity ?: return
        coroutineScope.launch {
            when (val result = googleAuthManager.signIn(currentActivity)) {
                is GoogleSignInResult.Success -> {
                    viewModel.googleSignIn(
                        idToken = result.idToken,
                        email = result.email,
                        name = result.displayName,
                        avatarUrl = result.profilePictureUrl
                    )
                }
                is GoogleSignInResult.Cancelled -> {
                    viewModel.onGoogleSignInCancelled()
                }
                is GoogleSignInResult.Error -> {
                    viewModel.onGoogleSignInError(result.message)
                }
            }
        }
    }

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onLoginSuccess()
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(top = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // --- CABEÇALHO ---
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
                            Text(text = "🔐", fontSize = 36.sp)
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Bem-vindo!",
                            color = AuthColors.TextPrimary,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Faça login para sincronizar seu histórico",
                            color = AuthColors.TextSecondary,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))

                // --- FORMULÁRIO ---
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(500, delayMillis = 200)) + slideInVertically(initialOffsetY = { 50 })
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {

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
                            imeAction = ImeAction.Next,
                            isError = emailError != null,
                            errorMessage = emailError
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        AuthTextField(
                            value = password,
                            onValueChange = {
                                password = it
                                viewModel.validatePassword(it)
                            },
                            label = "Senha",
                            leadingIcon = Icons.Outlined.Lock,
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done,
                            isError = passwordError != null,
                            errorMessage = passwordError,
                            isPassword = true,
                            onImeAction = {
                                focusManager.clearFocus()
                                if (emailError == null && passwordError == null &&
                                    email.isNotEmpty() && password.isNotEmpty()) {
                                    viewModel.login(email, password)
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Opções
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.clickable { rememberMe = !rememberMe },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = rememberMe,
                                    onCheckedChange = { rememberMe = it },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = AuthColors.Primary,
                                        uncheckedColor = AuthColors.Border
                                    )
                                )
                                Text(
                                    text = "Lembrar-me",
                                    color = AuthColors.TextSecondary,
                                    fontSize = 14.sp
                                )
                            }
                            Text(
                                text = "Esqueceu a senha?",
                                color = AuthColors.Primary,
                                fontSize = 14.sp,
                                modifier = Modifier.clickable { onNavigateToForgotPassword() }
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        AuthPrimaryButton(
                            text = "Fazer login",
                            onClick = {
                                focusManager.clearFocus()
                                viewModel.login(email, password)
                            },
                            enabled = email.isNotEmpty() && password.isNotEmpty(),
                            isLoading = authState is AuthState.Loading
                        )

                        Spacer(modifier = Modifier.height(24.dp))
                        AuthDivider(text = "Ou continue com")
                        Spacer(modifier = Modifier.height(24.dp))

                        // --- BOTÃO DO GOOGLE ---
                        GoogleSignInButton(
                            onClick = { startGoogleSignIn() },
                            isLoading = authState is AuthState.GoogleLoading,
                            enabled = authState !is AuthState.Loading && authState !is AuthState.GoogleLoading
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // --- BOTÃO DE PULAR / MODO OFFLINE ---
                        OutlinedButton(
                            onClick = onSkipLogin,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = AuthColors.TextPrimary,
                                containerColor = Color.Transparent
                            ),
                            border = BorderStroke(1.dp, AuthColors.Primary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.NoAccounts,
                                contentDescription = null,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text("Continuar sem login (Offline)")
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Não tem uma conta? ",
                                color = AuthColors.TextSecondary,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Criar conta",
                                color = AuthColors.Primary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.clickable { onNavigateToRegister() }
                            )
                        }
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
    }
}