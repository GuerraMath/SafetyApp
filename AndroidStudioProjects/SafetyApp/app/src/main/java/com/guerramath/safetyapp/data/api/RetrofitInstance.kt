package com.guerramath.safetyapp.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {

    // Ajuste a URL se necessário (10.0.2.2 é para o emulador Android acessar o localhost do PC)
    private const val BASE_URL = "http://10.0.2.2:8080/api/v1/"

    // Configuração do Log (Simplificado para evitar erro de BuildConfig)
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        // Loga o corpo das requisições para facilitar o debug
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Cliente HTTP com timeouts e interceptor
    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    // --- CORREÇÃO PRINCIPAL ---
    // A variável 'retrofit' deve ser pública (sem 'private')
    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client) // Agora o 'client' será encontrado corretamente
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // Se você tiver um serviço genérico antigo, pode manter ou remover:
    // val api: SafetyApiService by lazy { retrofit.create(SafetyApiService::class.java) }
}