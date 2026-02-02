package com.guerramath.safetyapp.core.network

/**
 * Wrapper genérico para resultados de chamadas de rede.
 * Encapsula sucesso, erro e estado de loading.
 */
sealed class NetworkResult<out T> {

    // Estado de Sucesso: contém os dados
    data class Success<T>(val data: T) : NetworkResult<T>()

    // Estado de Erro: contém mensagem e opcionais (código e exceção)
    data class Error(
        val message: String,
        val code: Int? = null,
        val exception: Throwable? = null
    ) : NetworkResult<Nothing>()

    // Estado de Carregamento
    data object Loading : NetworkResult<Nothing>()

    // --- Helpers para verificar estado ---
    val isSuccess: Boolean get() = this is Success
    val isError: Boolean get() = this is Error
    val isLoading: Boolean get() = this is Loading

    // --- Helpers Funcionais ---

    // Obter dados de forma segura
    fun getOrNull(): T? = (this as? Success)?.data

    // Obter dados ou valor padrão
    fun getOrDefault(default: @UnsafeVariance T): T = getOrNull() ?: default

    // Mapear resultado (Transformar T em R, mantendo Erro/Loading se existirem)
    fun <R> map(transform: (T) -> R): NetworkResult<R> = when (this) {
        is Success -> Success(transform(data))
        is Error -> this
        is Loading -> Loading
    }

    // Executar ação em caso de sucesso (Ex: logar sucesso)
    inline fun onSuccess(action: (T) -> Unit): NetworkResult<T> {
        if (this is Success) action(data)
        return this
    }

    // Executar ação em caso de erro (Ex: mostrar Toast)
    inline fun onError(action: (String, Int?, Throwable?) -> Unit): NetworkResult<T> {
        if (this is Error) action(message, code, exception)
        return this
    }
}