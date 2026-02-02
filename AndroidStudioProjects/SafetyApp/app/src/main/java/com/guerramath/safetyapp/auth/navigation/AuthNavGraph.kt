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
import com.guerramath.safetyapp.data.api.RetrofitInstance // Verifique se o nome é RetrofitInstance ou RetrofitClient

@Composable
fun AuthNavGraph(
    navController: NavHostController,
    onLoginSuccess: () -> Unit // Callback para ir para a Home
) {
    val context = LocalContext.current

    // 1. Criar Dependências
    val authPreferences = remember { AuthPreferences(context) }

    // Cria o serviço da API usando o RetrofitInstance que você já tem
    val apiService = remember {
        // Se RetrofitInstance.api não existir, use .retrofit.create(...)
        RetrofitInstance.retrofit.create(AuthApiService::class.java)
    }

    // Cria o Repositório
    val repository = remember { AuthRepository(apiService, authPreferences) }

    // 2. Criar ViewModel usando a Factory CORRETA (passando o repository)
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(repository)
    )

    NavHost(navController = navController, startDestination = "login") {

        composable("login") {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateToRegister = { navController.navigate("register") },
                onNavigateToForgotPassword = { navController.navigate("forgot_password") },
                onLoginSuccess = onLoginSuccess
            )
        }

        composable("register") {
            RegisterScreen(
                viewModel = authViewModel,
                onNavigateBack = { navController.popBackStack() },
                onRegisterSuccess = {
                    // Volta para o login após registro ou loga direto
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