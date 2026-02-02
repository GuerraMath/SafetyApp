
package com.guerramath.safetyapp.core.network

import com.google.gson.annotations.SerializedName

/**
 * Modelo padrão de resposta de erro da API.
 * Ajuste conforme o formato da sua API.
 */
data class ErrorResponse(
    @SerializedName("message")
    val message: String? = null,

    @SerializedName("error")
    val error: String? = null,

    @SerializedName("errors")
    val errors: Map<String, List<String>>? = null,

    @SerializedName("status")
    val status: Int? = null,

    @SerializedName("code")
    val code: String? = null
) {
    /** Retorna a mensagem de erro mais apropriada */
    fun getDisplayMessage(): String {
        return message
            ?: error
            ?: errors?.values?.flatten()?.firstOrNull()
            ?: "Ocorreu um erro"
    }
}
