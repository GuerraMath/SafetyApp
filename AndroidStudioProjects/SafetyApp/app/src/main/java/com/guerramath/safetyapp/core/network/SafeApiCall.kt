
package com.guerramath.safetyapp.core.network

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Executa uma chamada de API de forma segura, tratando todos os erros possíveis.
 *
 * @param apiCall A função suspensa que faz a chamada de API
 * @return NetworkResult com sucesso ou erro tratado
 */
suspend fun <T> safeApiCall(
    apiCall: suspend () -> Response<T>
): NetworkResult<T> = withContext(Dispatchers.IO) {
    try {
        val response = apiCall()

        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                NetworkResult.Success(body)
            } else {
                NetworkResult.Error("Resposta vazia do servidor", response.code())
            }
        } else {
            handleHttpError(response)
        }
    } catch (e: ApiException) {
        // Exceções já tratadas
        NetworkResult.Error(e.message ?: "Erro desconhecido", e.code, e)
    } catch (e: HttpException) {
        handleHttpException(e)
    } catch (e: SocketTimeoutException) {
        NetworkResult.Error(
            "Tempo de conexão esgotado. Tente novamente.",
            exception = e
        )
    } catch (e: UnknownHostException) {
        NetworkResult.Error(
            "Não foi possível conectar ao servidor. Verifique sua internet.",
            exception = e
        )
    } catch (e: IOException) {
        NetworkResult.Error(
            "Erro de conexão. Verifique sua internet.",
            exception = e
        )
    } catch (e: JsonSyntaxException) {
        NetworkResult.Error(
            "Erro ao processar resposta do servidor.",
            exception = e
        )
    } catch (e: Exception) {
        NetworkResult.Error(
            e.message ?: "Ocorreu um erro inesperado.",
            exception = e
        )
    }
}

/** Trata erros HTTP baseado no código de status */
private fun <T> handleHttpError(response: Response<T>): NetworkResult.Error {
    val errorBody = response.errorBody()?.string()
    val errorResponse = parseErrorResponse(errorBody)
    val message = errorResponse?.getDisplayMessage()

    return when (response.code()) {
        400 -> NetworkResult.Error(message ?: "Requisição inválida.", 400)
        401 -> NetworkResult.Error(message ?: "Não autorizado. Faça login novamente.", 401)
        403 -> NetworkResult.Error(message ?: "Acesso negado.", 403)
        404 -> NetworkResult.Error(message ?: "Recurso não encontrado.", 404)
        409 -> NetworkResult.Error(message ?: "Conflito. Este recurso já existe.", 409)
        422 -> NetworkResult.Error(message ?: "Dados inválidos.", 422)
        429 -> NetworkResult.Error("Muitas requisições. Aguarde um momento.", 429)
        in 500..599 -> NetworkResult.Error(
            message ?: "Erro no servidor. Tente novamente mais tarde.",
            response.code()
        )
        else -> NetworkResult.Error(
            message ?: "Erro desconhecido (${response.code()})",
            response.code()
        )
    }
}

/** Trata exceções HTTP do Retrofit */
private fun handleHttpException(e: HttpException): NetworkResult.Error {
    val errorBody = e.response()?.errorBody()?.string()
    val errorResponse = parseErrorResponse(errorBody)
    val message = errorResponse?.getDisplayMessage()

    return when (e.code()) {
        401 -> NetworkResult.Error(message ?: "Sessão expirada. Faça login novamente.", 401, e)
        403 -> NetworkResult.Error(message ?: "Acesso negado.", 403, e)
        404 -> NetworkResult.Error(message ?: "Recurso não encontrado.", 404, e)
        in 500..599 -> NetworkResult.Error(message ?: "Erro no servidor.", e.code(), e)
        else -> NetworkResult.Error(message ?: "Erro HTTP ${e.code()}", e.code(), e)
    }
}

/** Tenta fazer parse do corpo de erro da API */
private fun parseErrorResponse(errorBody: String?): ErrorResponse? {
    if (errorBody.isNullOrEmpty()) return null

    return try {
        Gson().fromJson(errorBody, ErrorResponse::class.java)
    } catch (e: Exception) {
        null
    }
}
