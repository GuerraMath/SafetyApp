package com.guerramath.safetyapp.auth.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.guerramath.safetyapp.auth.data.api.AuthApiService
import com.guerramath.safetyapp.auth.data.preferences.AuthPreferences
import com.guerramath.safetyapp.auth.data.repository.AuthRepository
import com.guerramath.safetyapp.auth.ui.screens.ForgotPasswordScreen
import com.guerramath.safetyapp.auth.ui.screens.LoginScreen
import com.guerramath.safetyapp.auth.ui.screens.RegisterScreen
import com.guerramath.safetyapp.auth.viewmodel.AuthViewModel
import com.guerramath.safetyapp.auth.viewmodel.AuthViewModelFactory
import com.guerramath.safetyapp.data.api.RetrofitInstance

@Composable
fun AuthNavGraph(
    navController: NavHostController,
    onLoginSuccess: () -> Unit,
    onSkipLogin: () -> Unit // NOVO PARÂMETRO
) {
    val context = LocalContext.current

    // Dependências
    val authPreferences = remember { AuthPreferences(context) }
    val apiService = remember {
        RetrofitInstance.retrofit.create(AuthApiService::class.java)
    }
    val repository = remember { AuthRepository(apiService, authPreferences) }

    // ViewModel
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(repository)
    )

    NavHost(navController = navController, startDestination = "login") {

        composable("login") {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateToRegister = { navController.navigate("register") },
                onNavigateToForgotPassword = { navController.navigate("forgot_password") },
                onLoginSuccess = onLoginSuccess,
                onSkipLogin = onSkipLogin // Repassa a ação
            )
        }

        composable("register") {
            RegisterScreen(
                viewModel = authViewModel,
                onNavigateBack = { navController.popBackStack() },
                onRegisterSuccess = {
                    navController.popBackStack()
                }
            )
        }

        composable("forgot_password") {
            ForgotPasswordScreen(
                viewModel = authViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}