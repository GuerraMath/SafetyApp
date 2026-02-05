package com.guerramath.safetyapp.auth.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guerramath.safetyapp.auth.data.model.User
import com.guerramath.safetyapp.auth.data.repository.AuthRepository
import com.guerramath.safetyapp.core.network.NetworkResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Estados de Autenticação
sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: User) : AuthState() // Mudado de LoggedIn para Success para bater com a Screen
    data class Error(val message: String) : AuthState()
    data class ForgotPasswordSent(val message: String) : AuthState()
}

class AuthViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    // --- ESTADOS ---
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    // Validação de Email
    private val _emailError = MutableStateFlow<String?>(null)
    val emailError = _emailError.asStateFlow()

    // Validação de Senha
    private val _passwordError = MutableStateFlow<String?>(null)
    val passwordError = _passwordError.asStateFlow()

    // Validação de Nome (NOVO)
    private val _nameError = MutableStateFlow<String?>(null)
    val nameError = _nameError.asStateFlow()

    // Validação de Confirmação de Senha (NOVO)
    private val _confirmPasswordError = MutableStateFlow<String?>(null)
    val confirmPasswordError = _confirmPasswordError.asStateFlow()

    // --- AÇÕES ---

    fun login(email: String, password: String) {
        if (!validateLoginInput(email, password)) return

        viewModelScope.launch {
            _authState.value = AuthState.Loading

            when (val result = repository.login(email, password)) {
                is NetworkResult.Success -> {
                    _authState.value = AuthState.Success(result.data)
                }
                is NetworkResult.Error -> {
                    _authState.value = AuthState.Error(result.message ?: "Erro ao fazer login")
                }
                is NetworkResult.Loading -> {
                    _authState.value = AuthState.Loading
                }
            }
        }
    }

    fun oauthLogin(idToken: String) {
        Log.d("AuthViewModel", "Iniciando OAuth login com token de ${idToken.length} caracteres")
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            when (val result = repository.oauthLogin(idToken)) {
                is NetworkResult.Success -> {
                    Log.i("AuthViewModel", "OAuth login bem-sucedido para ${result.data.email}")
                    _authState.value = AuthState.Success(result.data)
                }
                is NetworkResult.Error -> {
                    val errorMsg = result.message ?: "Erro ao fazer login com Google"
                    Log.e("AuthViewModel", "Erro OAuth login: $errorMsg (código: ${result.code})")
                    _authState.value = AuthState.Error(errorMsg)
                }
                is NetworkResult.Loading -> {
                    _authState.value = AuthState.Loading
                }
            }
        }
    }

    /**
     * Define erro de OAuth diretamente (p.ex, cancelamento do usuário ou erro do Google)
     */
    fun setOAuthError(errorMessage: String) {
        Log.e("AuthViewModel", "Erro OAuth: $errorMessage")
        _authState.value = AuthState.Error(errorMessage)
    }

    // Assinatura atualizada para receber os 4 parâmetros da RegisterScreen
    fun register(email: String, password: String, name: String, confirmPassword: String) {
        // Valida tudo antes de enviar
        validateName(name)
        validateEmail(email)
        validatePassword(password)
        validateConfirmPassword(password, confirmPassword)

        // Se tiver algum erro, para aqui
        if (_nameError.value != null || _emailError.value != null ||
            _passwordError.value != null || _confirmPasswordError.value != null) {
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading

            // Chama o repositório (ordem dos parâmetros depende do seu Repository)
            when (val result = repository.register(name, email, password)) {
                is NetworkResult.Success -> {
                    _authState.value = AuthState.Success(result.data)
                }
                is NetworkResult.Error -> {
                    _authState.value = AuthState.Error(result.message ?: "Erro ao criar conta")
                }
                is NetworkResult.Loading -> {
                    _authState.value = AuthState.Loading
                }
            }
        }
    }

    fun forgotPassword(email: String) {
        if (email.isBlank()) {
            _emailError.value = "Digite seu email"
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading

            when (val result = repository.forgotPassword(email)) {
                is NetworkResult.Success -> {
                    _authState.value = AuthState.ForgotPasswordSent(result.data)
                }
                is NetworkResult.Error -> {
                    _authState.value = AuthState.Error(result.message ?: "Erro ao recuperar senha")
                }
                is NetworkResult.Loading -> {
                    _authState.value = AuthState.Loading
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _authState.value = AuthState.Idle
        }
    }

    // --- VALIDAÇÕES ---

    fun validateName(name: String) {
        _nameError.value = if (name.isBlank()) "Nome é obrigatório" else null
    }

    fun validateEmail(email: String) {
        if (email.isBlank()) {
            _emailError.value = "Email é obrigatório"
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailError.value = "Email inválido"
        } else {
            _emailError.value = null
        }
    }

    fun validatePassword(password: String) {
        if (password.length < 6) {
            _passwordError.value = "Mínimo 6 caracteres"
        } else {
            _passwordError.value = null
        }
    }

    fun validateConfirmPassword(password: String, confirm: String) {
        if (confirm.isBlank()) {
            _confirmPasswordError.value = "Confirmação necessária"
        } else if (password != confirm) {
            _confirmPasswordError.value = "Senhas não conferem"
        } else {
            _confirmPasswordError.value = null
        }
    }

    private fun validateLoginInput(email: String, password: String): Boolean {
        validateEmail(email)
        validatePassword(password)
        return _emailError.value == null && _passwordError.value == null
    }

    fun resetState() {
        _authState.value = AuthState.Idle
        _emailError.value = null
        _passwordError.value = null
        _nameError.value = null
        _confirmPasswordError.value = null
    }
}