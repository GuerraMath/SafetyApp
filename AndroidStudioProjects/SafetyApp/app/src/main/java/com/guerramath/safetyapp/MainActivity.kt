package com.guerramath.safetyapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
// import com.guerramath.safetyapp.auth.biometric.BiometricPreferences // Se você tiver este arquivo, descomente
import com.guerramath.safetyapp.auth.data.preferences.AuthPreferences
// import com.guerramath.safetyapp.auth.data.preferences.UserPreferences // REMOVIDO: Integrado no AuthPreferences
import com.guerramath.safetyapp.auth.navigation.AuthNavGraph
import com.guerramath.safetyapp.auth.session.SessionManager
import com.guerramath.safetyapp.ui.screens.HomeScreen
// import com.guerramath.safetyapp.ui.screens.SplashScreen // Certifique-se que este arquivo existe
// import com.guerramath.safetyapp.auth.ui.screens.BiometricLoginScreen // Certifique-se que este arquivo existe
import com.guerramath.safetyapp.ui.theme.SafetyAppTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var authPreferences: AuthPreferences
    // private lateinit var biometricPreferences: BiometricPreferences // Descomente se tiver a classe

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inicializa preferências
        authPreferences = AuthPreferences(this)
        // biometricPreferences = BiometricPreferences(this) // Descomente se tiver

        // Inicializa SessionManager
        SessionManager.init(this)

        setContent {
            SafetyAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainNavigation()
                }
            }
        }
    }

    @Composable
    private fun MainNavigation() {
        val navController = rememberNavController()

        // Vamos iniciar direto no Auth ou Home por enquanto para simplificar e evitar erros de Splash
        // Se você tiver a SplashScreen, pode descomentar a lógica abaixo
        val startDestination = "auth"

        NavHost(
            navController = navController,
            startDestination = startDestination
        ) {

            /* // --- SPLASH SCREEN (Descomente se tiver o arquivo) ---
            composable("splash") {
                SplashScreen(
                    onNavigateToLogin = {
                        navController.navigate("auth") {
                            popUpTo("splash") { inclusive = true }
                        }
                    },
                    onNavigateToHome = {
                        navController.navigate("home") {
                            popUpTo("splash") { inclusive = true }
                        }
                    },
                    checkSession = {
                        // Verifica sessão usando AuthPreferences
                        authPreferences.getAccessToken() != null
                    }
                )
            }
            */

            /*
            // --- BIOMETRIC LOGIN (Descomente se tiver o arquivo) ---
            composable("biometric_login") {
                // Recupera dados do AuthPreferences (não mais UserPreferences)
                val userName by authPreferences.userName.collectAsState(initial = "Usuário")
                val userEmail by authPreferences.userEmail.collectAsState(initial = "")

                BiometricLoginScreen(
                    userName = userName ?: "Usuário",
                    userEmail = userEmail ?: "",
                    onBiometricSuccess = {
                        navController.navigate("home") {
                            popUpTo("biometric_login") { inclusive = true }
                        }
                    },
                    onUsePassword = {
                        navController.navigate("auth") {
                            popUpTo("biometric_login") { inclusive = true }
                        }
                    },
                    onSwitchAccount = {
                        lifecycleScope.launch {
                            authPreferences.clearAuthData()
                        }
                        navController.navigate("auth") {
                            popUpTo("biometric_login") { inclusive = true }
                        }
                    }
                )
            }
            */

            // --- FLUXO DE AUTENTICAÇÃO ---
            composable("auth") {
                // Criamos um navController separado para o grafo de autenticação interno
                val authNavController = rememberNavController()

                AuthNavGraph(
                    navController = authNavController, // Passamos o controller
                    onLoginSuccess = { // CORREÇÃO: Nome do parâmetro correto
                        navController.navigate("home") {
                            popUpTo("auth") { inclusive = true }
                        }
                    }
                )
            }

            // --- HOME SCREEN ---
            composable("home") {
                HomeScreen(
                    onLogout = {
                        navController.navigate("auth") {
                            popUpTo("home") { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}