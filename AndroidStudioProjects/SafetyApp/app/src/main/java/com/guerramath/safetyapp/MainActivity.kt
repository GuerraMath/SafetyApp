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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.guerramath.safetyapp.auth.data.preferences.AuthPreferences
import com.guerramath.safetyapp.auth.navigation.AuthNavGraph
import com.guerramath.safetyapp.auth.session.SessionManager
import com.guerramath.safetyapp.ui.screens.HomeScreen
import com.guerramath.safetyapp.ui.theme.SafetyAppTheme

class MainActivity : ComponentActivity() {

    private lateinit var authPreferences: AuthPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        authPreferences = AuthPreferences(this)
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

        // Define 'auth' como inicio, mas o usuário pode pular nela
        val startDestination = "auth"

        NavHost(
            navController = navController,
            startDestination = startDestination
        ) {

            // --- FLUXO DE AUTENTICAÇÃO ---
            composable("auth") {
                val authNavController = rememberNavController()

                AuthNavGraph(
                    navController = authNavController,
                    onLoginSuccess = {
                        navController.navigate("home") {
                            popUpTo("auth") { inclusive = true }
                        }
                    },
                    // AÇÃO DE PULAR LOGIN: Vai para Home direto
                    onSkipLogin = {
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