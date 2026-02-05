package com.guerramath.safetyapp.auth.data.repository

import com.guerramath.safetyapp.auth.data.dto.ForgotPasswordRequest
import com.guerramath.safetyapp.auth.data.dto.GoogleSignInRequest
import com.guerramath.safetyapp.auth.data.dto.LoginRequest
import com.guerramath.safetyapp.auth.data.dto.OAuthLoginRequest
import com.guerramath.safetyapp.auth.data.dto.RegisterRequest
import com.guerramath.safetyapp.auth.data.api.AuthApiService
import com.guerramath.safetyapp.auth.data.model.User
import com.guerramath.safetyapp.auth.data.preferences.AuthPreferences
import com.guerramath.safetyapp.core.network.NetworkResult
import com.guerramath.safetyapp.core.network.safeApiCall

class AuthRepository(
    private val apiService: AuthApiService,
    private val authPreferences: AuthPreferences
) {

    /**
     * Realiza login do usuário.
     */
    suspend fun login(email: String, password: String): NetworkResult<User> {
        val result = safeApiCall {
            apiService.login(LoginRequest(email, password))
        }

        return when (result) {
            is NetworkResult.Success -> {
                val response = result.data

                // Salva os dados na persistência local
                authPreferences.saveAuthData(
                    token = response.token,
                    refreshToken = response.refreshToken,
                    userId = response.user.id.toString(), // Garante conversão Int -> String
                    name = response.user.name,
                    email = response.user.email
                )

                // Retorna o objeto User para a UI
                NetworkResult.Success(response.user)
            }
            is NetworkResult.Error -> {
                // Repassa o erro mantendo a mensagem e código
                NetworkResult.Error(result.message, result.code, result.exception)
            }
            is NetworkResult.Loading -> NetworkResult.Loading
        }
    }

    /**
     * Realiza login via OAuth (Google).
     */
    suspend fun oauthLogin(idToken: String): NetworkResult<User> {
        val result = safeApiCall {
            apiService.oauthLogin(OAuthLoginRequest(idToken))
        }

        return when (result) {
            is NetworkResult.Success -> {
                val response = result.data

                authPreferences.saveAuthData(
                    token = response.token,
                    refreshToken = response.refreshToken,
                    userId = response.user.id.toString(),
                    name = response.user.name,
                    email = response.user.email
                )

                NetworkResult.Success(response.user)
            }
            is NetworkResult.Error -> {
                NetworkResult.Error(result.message, result.code, result.exception)
            }
            is NetworkResult.Loading -> NetworkResult.Loading
        }
    }

    /**
     * Registra novo usuário.
     */
    suspend fun register(name: String, email: String, password: String): NetworkResult<User> {
        val result = safeApiCall {
            apiService.register(RegisterRequest(name, email, password))
        }

        return when (result) {
            is NetworkResult.Success -> {
                val response = result.data

                authPreferences.saveAuthData(
                    token = response.token,
                    refreshToken = response.refreshToken,
                    userId = response.user.id.toString(),
                    name = response.user.name,
                    email = response.user.email
                )

                NetworkResult.Success(response.user)
            }
            is NetworkResult.Error -> {
                NetworkResult.Error(result.message, result.code, result.exception)
            }
            is NetworkResult.Loading -> NetworkResult.Loading
        }
    }

    /**
     * Solicita recuperação de senha.
     */
    suspend fun forgotPassword(email: String): NetworkResult<String> {
        val result = safeApiCall {
            apiService.forgotPassword(ForgotPasswordRequest(email))
        }

        return when (result) {
            is NetworkResult.Success -> NetworkResult.Success(result.data.message)
            is NetworkResult.Error -> NetworkResult.Error(result.message, result.code, result.exception)
            is NetworkResult.Loading -> NetworkResult.Loading
        }
    }

    /**
     * Faz logout do usuário.
     */
    suspend fun logout(): NetworkResult<Unit> {
        val result = safeApiCall {
            apiService.logout()
        }

        // Limpa dados locais independente do sucesso da API
        authPreferences.clearAuthData()

        return when (result) {
            is NetworkResult.Success -> NetworkResult.Success(Unit)
            is NetworkResult.Error -> NetworkResult.Error(result.message, result.code, result.exception)
            is NetworkResult.Loading -> NetworkResult.Loading
        }
    }

    /**
     * Realiza login via Google.
     * Envia o ID Token para o backend validar e retorna o usuário autenticado.
     */
    suspend fun googleSignIn(
        idToken: String,
        email: String,
        name: String?,
        avatarUrl: String?
    ): NetworkResult<User> {
        val result = safeApiCall {
            apiService.googleSignIn(
                GoogleSignInRequest(
                    idToken = idToken,
                    email = email,
                    name = name,
                    avatarUrl = avatarUrl
                )
            )
        }

        return when (result) {
            is NetworkResult.Success -> {
                val response = result.data

                // Salva os dados na persistência local
                authPreferences.saveAuthData(
                    token = response.token,
                    refreshToken = response.refreshToken,
                    userId = response.user.id.toString(),
                    name = response.user.name,
                    email = response.user.email
                )

                NetworkResult.Success(response.user)
            }
            is NetworkResult.Error -> {
                NetworkResult.Error(result.message, result.code, result.exception)
            }
            is NetworkResult.Loading -> NetworkResult.Loading
        }
    }

    // Expondo o estado de login através das preferências
    val isLoggedIn = authPreferences.isLoggedIn
}