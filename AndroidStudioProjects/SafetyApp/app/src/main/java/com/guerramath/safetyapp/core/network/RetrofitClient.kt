
package com.guerramath.safetyapp.core.network

import android.content.Context
import com.google.gson.GsonBuilder
import com.guerramath.safetyapp.BuildConfig
import com.guerramath.safetyapp.auth.data.token.TokenManager
import com.guerramath.safetyapp.core.network.interceptors.AuthInterceptor
import com.guerramath.safetyapp.core.network.interceptors.LoggingInterceptor
import com.guerramath.safetyapp.core.network.interceptors.NetworkInterceptor
import com.guerramath.safetyapp.core.network.interceptors.TokenRefreshInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Cliente Retrofit singleton com todas as configurações.
 *
 * Uso:
 * ```
 * val apiService = RetrofitClient.getInstance(context)
 *     .create(SeuApiService::class.java)
 * ```
 */
object RetrofitClient {

    // ⚠️ ALTERE PARA SUA URL DE API
    private const val BASE_URL = "https://sua-api.com/api/"

    // Timeouts
    private const val CONNECT_TIMEOUT = 30L
    private const val READ_TIMEOUT = 30L
    private const val WRITE_TIMEOUT = 30L

    @Volatile
    private var retrofit: Retrofit? = null

    @Volatile
    private var tokenManager: TokenManager? = null

    /**
     * Obtém a instância do Retrofit configurada.
     * Thread-safe usando double-checked locking.
     */
    fun getInstance(context: Context): Retrofit {
        return retrofit ?: synchronized(this) {
            retrofit ?: buildRetrofit(context.applicationContext).also {
                retrofit = it
            }
        }
    }

    /** Obtém o TokenManager (para uso em logout, etc.) */
    fun getTokenManager(context: Context): TokenManager {
        return tokenManager ?: synchronized(this) {
            tokenManager ?: TokenManager(context.applicationContext).also {
                tokenManager = it
            }
        }
    }

    /** Limpa a instância (útil para testes ou reset) */
    fun clearInstance() {
        synchronized(this) {
            retrofit = null
            tokenManager = null
        }
    }

    private fun buildRetrofit(context: Context): Retrofit {
        val tm = getTokenManager(context)

        val okHttpClient = OkHttpClient.Builder()
            // Timeouts
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)

            // Interceptor de conectividade (primeiro!)
            .addInterceptor(NetworkInterceptor(context))

            // Interceptor de autenticação
            .addInterceptor(AuthInterceptor(tm))

            // Interceptor de refresh token
            .addInterceptor(TokenRefreshInterceptor(tm, BASE_URL))

            // Logging apenas em debug
            .apply {
                if (BuildConfig.DEBUG) {
                    addInterceptor(LoggingInterceptor())
                }
            }

            .build()

        val gson = GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            .setLenient()
            .create()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
}
