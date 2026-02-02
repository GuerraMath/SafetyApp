package com.guerramath.safetyapp.auth.navigation

// Rotas de navegação do módulo de autenticação
sealed class AuthRoute(val route: String) {
    object Login : AuthRoute("login")
    object Register : AuthRoute("register")
    object ForgotPassword : AuthRoute("forgot_password")
    object Home : AuthRoute("home") // Tela após login bem-sucedido
}
