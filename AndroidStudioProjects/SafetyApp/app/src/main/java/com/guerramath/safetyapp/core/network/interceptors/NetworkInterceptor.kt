
package com.guerramath.safetyapp.core.network.interceptors

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.guerramath.safetyapp.core.network.ApiException
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor que verifica conectividade antes de fazer requisições.
 * Lança exceção apropriada se não houver conexão.
 */
class NetworkInterceptor(
    private val context: Context
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        if (!isNetworkAvailable()) {
            throw ApiException.NetworkException(
                "Sem conexão com a internet. Verifique sua conexão e tente novamente."
            )
        }
        return chain.proceed(chain.request())
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager

        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
}

