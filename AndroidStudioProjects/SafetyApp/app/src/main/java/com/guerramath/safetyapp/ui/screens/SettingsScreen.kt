
package com.guerramath.safetyapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import com.guerramath.safetyapp.auth.biometric.BiometricAuthManager
import com.guerramath.safetyapp.auth.biometric.BiometricPreferences
import com.guerramath.safetyapp.auth.biometric.BiometricResult
import com.guerramath.safetyapp.auth.ui.components.AuthColors
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? FragmentActivity
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    val biometricManager = remember { BiometricAuthManager(context) }
    val biometricPreferences = remember { BiometricPreferences(context) }

    // Estados
    var isBiometricAvailable by remember { mutableStateOf(false) }
    var biometricEnabled by remember { mutableStateOf(false) }
    var biometricForLogin by remember { mutableStateOf(false) }
    var showBiometricError by remember { mutableStateOf<String?>(null) }

    // Carrega configurações
    LaunchedEffect(Unit) {
        isBiometricAvailable = biometricManager.isBiometricAvailable()
        biometricEnabled = biometricPreferences.isBiometricEnabled.first()
        biometricForLogin = biometricPreferences.useBiometricForLogin.first()
    }

    // Função para habilitar biometria (requer autenticação)
    fun enableBiometric() {
        if (activity == null) return

        biometricManager.authenticate(
            activity = activity,
            title = "Confirmar identidade",
            subtitle = "Autentique para habilitar biometria",
            onResult = { result ->
                when (result) {
                    is BiometricResult.Success -> {
                        scope.launch {
                            biometricPreferences.setBiometricEnabled(true)
                            biometricPreferences.setUseBiometricForLogin(true)
                            biometricEnabled = true
                            biometricForLogin = true
                        }
                    }
                    is BiometricResult.Error -> {
                        showBiometricError = result.message
                    }
                    else -> {}
                }
            }
        )
    }

    Column(
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
        // Top Bar
        TopAppBar(
            title = {
                Text(
                    text = "Configurações",
                    color = AuthColors.TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
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
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp)
        ) {
            // Seção: Segurança
            SettingsSection(title = "Segurança") {
                // Biometria
                if (isBiometricAvailable || biometricManager.hasBiometricCapability()) {
                    SettingsSwitch(
                        icon = Icons.Default.Fingerprint,
                        title = "Autenticação Biométrica",
                        subtitle = if (isBiometricAvailable) {
                            "Use digital ou rosto para entrar"
                        } else {
                            "Configure sua biometria nas configurações do dispositivo"
                        },
                        checked = biometricEnabled,
                        enabled = isBiometricAvailable,
                        onCheckedChange = { enabled ->
                            if (enabled) {
                                enableBiometric()
                            } else {
                                scope.launch {
                                    biometricPreferences.setBiometricEnabled(false)
                                    biometricPreferences.setUseBiometricForLogin(false)
                                    biometricEnabled = false
                                    biometricForLogin = false
                                }
                            }
                        }
                    )

                    if (biometricEnabled) {
                        SettingsSwitch(
                            icon = Icons.Default.Login,
                            title = "Biometria no Login",
                            subtitle = "Pedir biometria ao abrir o app",
                            checked = biometricForLogin,
                            onCheckedChange = { enabled ->
                                scope.launch {
                                    biometricPreferences.setUseBiometricForLogin(enabled)
                                    biometricForLogin = enabled
                                }
                            }
                        )
                    }
                }

                SettingsItem(
                    icon = Icons.Default.Lock,
                    title = "Alterar Senha",
                    subtitle = "Modifique sua senha de acesso",
                    onClick = { /* TODO: Navigate to change password */ }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Seção: Conta
            SettingsSection(title = "Conta") {
                SettingsItem(
                    icon = Icons.Default.Person,
                    title = "Editar Perfil",
                    subtitle = "Nome, email e foto",
                    onClick = { /* TODO: Navigate to edit profile */ }
                )

                SettingsItem(
                    icon = Icons.Default.Notifications,
                    title = "Notificações",
                    subtitle = "Configurar alertas e avisos",
                    onClick = { /* TODO: Navigate to notifications */ }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Seção: Sobre
            SettingsSection(title = "Sobre") {
                SettingsItem(
                    icon = Icons.Default.Info,
                    title = "Versão do App",
                    subtitle = "1.0.0",
                    onClick = { }
                )

                SettingsItem(
                    icon = Icons.Default.Description,
                    title = "Termos de Uso",
                    subtitle = "Leia nossos termos",
                    onClick = { /* TODO: Open terms */ }
                )

                SettingsItem(
                    icon = Icons.Default.PrivacyTip,
                    title = "Política de Privacidade",
                    subtitle = "Como usamos seus dados",
                    onClick = { /* TODO: Open privacy policy */ }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Botão de Logout
            Button(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AuthColors.Error.copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Logout,
                    contentDescription = null,
                    tint = AuthColors.Error
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Sair da conta",
                    color = AuthColors.Error,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // Snackbar de erro
    showBiometricError?.let { error ->
        LaunchedEffect(error) {
            kotlinx.coroutines.delay(3000)
            showBiometricError = null
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            text = title,
            color = AuthColors.Primary,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(AuthColors.Surface)
        ) {
            content()
        }
    }
}

@Composable
private fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = AuthColors.Primary,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = AuthColors.TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = subtitle,
                    color = AuthColors.TextSecondary,
                    fontSize = 14.sp
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = AuthColors.TextSecondary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun SettingsSwitch(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (enabled) AuthColors.Primary else AuthColors.TextSecondary,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = if (enabled) AuthColors.TextPrimary else AuthColors.TextSecondary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                color = AuthColors.TextSecondary,
                fontSize = 14.sp
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            colors = SwitchDefaults.colors(
                checkedThumbColor = AuthColors.Primary,
                checkedTrackColor = AuthColors.Primary.copy(alpha = 0.5f),
                uncheckedThumbColor = AuthColors.TextSecondary,
                uncheckedTrackColor = androidx.compose.ui.graphics.Color.White
            )
        )
    }
}

