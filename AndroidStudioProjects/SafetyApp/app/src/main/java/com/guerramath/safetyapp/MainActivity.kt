package com.guerramath.safetyapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.ApiException
import com.guerramath.safetyapp.auth.data.preferences.AuthPreferences
import com.guerramath.safetyapp.auth.navigation.AuthNavGraph
import com.guerramath.safetyapp.auth.oauth.GoogleSignInErrorHandler
import com.guerramath.safetyapp.auth.oauth.GoogleSignInHelper
import com.guerramath.safetyapp.auth.session.SessionManager
import com.guerramath.safetyapp.data.preferences.PreferencesManager
import com.guerramath.safetyapp.presentation.onboarding.OnboardingScreen
import com.guerramath.safetyapp.ui.screens.HomeScreen
import com.guerramath.safetyapp.ui.theme.SafetyAppTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {
    private val tag = "MainActivity"

    private lateinit var authPreferences: AuthPreferences
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var googleSignInHelper: GoogleSignInHelper
    private var onGoogleSignInResult: ((String?, String?) -> Unit)? = null

    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        Log.d(tag, "Resultado do Google Sign-In recebido")
        try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            val account = task.getResult(ApiException::class.java)
            val idToken = account?.idToken

            if (idToken != null) {
                Log.d(tag, "ID Token obtido com sucesso")
                onGoogleSignInResult?.invoke(idToken, null)
            } else {
                val errorMsg = "ID Token não foi retornado pelo Google"
                Log.e(tag, errorMsg)
                onGoogleSignInResult?.invoke(null, errorMsg)
            }
        } catch (e: ApiException) {
            val errorMsg = GoogleSignInErrorHandler.getErrorMessage(e)
            GoogleSignInErrorHandler.logDetailedError(e)
            Log.e(tag, "Erro ao obter ID Token: $errorMsg", e)
            onGoogleSignInResult?.invoke(null, errorMsg)
        } catch (e: Exception) {
            val errorMsg = "Erro inesperado: ${e.message}"
            Log.e(tag, errorMsg, e)
            onGoogleSignInResult?.invoke(null, errorMsg)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        Log.d(tag, "Inicializando MainActivity")

        authPreferences = AuthPreferences(this)
        preferencesManager = PreferencesManager(this)
        SessionManager.init(this)

        // Inicializar Google Sign-In Helper com client ID
        googleSignInHelper = GoogleSignInHelper(this, getString(R.string.google_client_id))
        Log.d(tag, "GoogleSignInHelper inicializado")

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
                    },
                    onGoogleSignInClick = { onGoogleSignInResult ->
                        // Valida Google Play Services antes de iniciar Sign-In
                        if (!validateGooglePlayServices()) {
                            val errorMsg = "Google Play Services não disponível. Por favor, atualize."
                            Log.w(tag, errorMsg)
                            onGoogleSignInResult?.invoke(null, errorMsg)
                            return@composable
                        }

                        Log.d(tag, "Google Play Services validado. Iniciando Sign-In...")

                        // Salva o callback e inicia o sign-in
                        this@MainActivity.onGoogleSignInResult = { idToken, error ->
                            onGoogleSignInResult?.invoke(idToken, error)
                        }
                        googleSignInLauncher.launch(googleSignInHelper.getSignInIntent())
                    }
                )
            }
        }
    }

    /**
     * Valida se Google Play Services está disponível e atualizado
     */
    private fun validateGooglePlayServices(): Boolean {
        Log.d(tag, "Validando Google Play Services...")
        val gapi = GoogleApiAvailability.getInstance()
        val code = gapi.isGooglePlayServicesAvailable(this)

        return when (code) {
            ConnectionResult.SUCCESS -> {
                Log.d(tag, "Google Play Services disponível e atualizado")
                true
            }
            else -> {
                val errorMsg = "Google Play Services não está disponível (código: $code)"
                Log.e(tag, errorMsg)

                if (gapi.isUserResolvableError(code)) {
                    Log.i(tag, "Erro é resolvível pelo usuário. Tentando mostrar diálogo...")
                    gapi.makeGooglePlayServicesAvailable(this)
                }
                false
            }
        }
    }
}
