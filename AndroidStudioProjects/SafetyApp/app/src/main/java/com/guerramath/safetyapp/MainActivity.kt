package com.guerramath.safetyapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.guerramath.safetyapp.auth.data.preferences.AuthPreferences
import com.guerramath.safetyapp.auth.navigation.AuthNavGraph
import com.guerramath.safetyapp.auth.session.SessionManager
import com.guerramath.safetyapp.data.preferences.PreferencesManager
import com.guerramath.safetyapp.presentation.onboarding.OnboardingScreen
import com.guerramath.safetyapp.ui.screens.HomeScreen
import com.guerramath.safetyapp.ui.theme.SafetyAppTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {

    private lateinit var authPreferences: AuthPreferences
    private lateinit var preferencesManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        authPreferences = AuthPreferences(this)
        preferencesManager = PreferencesManager(this)
        SessionManager.init(this)

        // Determinar destino inicial
        val startDestination = runBlocking {
            val onboardingCompleted = preferencesManager.isOnboardingCompleted.first()
            if (onboardingCompleted) "home" else "onboarding"
        }

        setContent {
            SafetyAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainNavigation(startDestination = startDestination)
                }
            }
        }
    }

    @Composable
    private fun MainNavigation(startDestination: String) {
        val navController = rememberNavController()

        NavHost(
            navController = navController,
            startDestination = startDestination
        ) {

            // --- ONBOARDING ---
            composable("onboarding") {
                OnboardingScreen(
                    onFinish = {
                        // Após onboarding, vai direto para Home (sem login obrigatório)
                        navController.navigate("home") {
                            popUpTo("onboarding") { inclusive = true }
                        }
                    }
                )
            }

            // --- HOME SCREEN (principal do app) ---
            composable("home") {
                HomeScreen(
                    onNavigateToLogin = {
                        // Quando usuário clica em "Fazer login" no drawer
                        navController.navigate("auth")
                    },
                    onLogout = {
                        // Permanece na Home, mas agora como não logado
                        // O DrawerContent já vai mostrar "Fazer login"
                    }
                )
            }

            // --- FLUXO DE AUTENTICAÇÃO ---
            composable("auth") {
                val authNavController = rememberNavController()

                AuthNavGraph(
                    navController = authNavController,
                    onLoginSuccess = {
                        // Após login bem-sucedido, volta para Home (agora logado)
                        navController.navigate("home") {
                            popUpTo("auth") { inclusive = true }
                        }
                    },
                    onSkipLogin = {
                        // Se usuário quiser voltar sem logar
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
