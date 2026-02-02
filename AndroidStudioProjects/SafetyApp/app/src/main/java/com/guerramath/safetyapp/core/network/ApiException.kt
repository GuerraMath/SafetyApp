package com.guerramath.safetyapp.core.network

import java.io.IOException

/**
 * Exceção base para erros de API.
 * Herda de IOException para ser tratada como erro de I/O pelo Retrofit/Coroutines.
 */
sealed class ApiException(
    override val message: String,
    val code: Int? = null,
    override val cause: Throwable? = null
) : IOException(message, cause) {

    /** Erro de rede (sem conexão, timeout, etc.) */
    class NetworkException(
        message: String = "Erro de conexão. Verifique sua internet.",
        cause: Throwable? = null
    ) : ApiException(message, null, cause)

    /** Erro de autenticação (401) */
    class UnauthorizedException(
        message: String = "Sessão expirada. Faça login novamente.",
        cause: Throwable? = null
    ) : ApiException(message, 401, cause)

    /** Erro de permissão (403) */
    class ForbiddenException(
        message: String = "Você não tem permissão para esta ação.",
        cause: Throwable? = null
    ) : ApiException(message, 403, cause)

    /** Recurso não encontrado (404) */
    class NotFoundException(
        message: String = "Recurso não encontrado.",
        cause: Throwable? = null
    ) : ApiException(message, 404, cause)

    /** Erro de validação (422) */
    class ValidationException(
        message: String = "Dados inválidos.",
        val errors: Map<String, List<String>>? = null,
        cause: Throwable? = null
    ) : ApiException(message, 422, cause)

    /** Erro do servidor (500+) */
    class ServerException(
        message: String = "Erro no servidor. Tente novamente mais tarde.",
        code: Int = 500,
        cause: Throwable? = null
    ) : ApiException(message, code, cause)

    /** Token expirado (precisa refresh) */
    class TokenExpiredException(
        message: String = "Token expirado.",
        cause: Throwable? = null
    ) : ApiException(message, 401, cause)

    /** Refresh token inválido (precisa relogar) - Usado pelo TokenRefreshInterceptor */
    class RefreshTokenInvalidException(
        message: String = "Sessão inválida. Faça login novamente.",
        cause: Throwable? = null
    ) : ApiException(message, 401, cause)

    /** Erro genérico/desconhecido */
    class UnknownException(
        message: String = "Ocorreu um erro inesperado.",
        code: Int? = null,
        cause: Throwable? = null
    ) : ApiException(message, code, cause)
}