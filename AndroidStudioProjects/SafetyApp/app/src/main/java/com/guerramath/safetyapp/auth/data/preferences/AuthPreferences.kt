package com.guerramath.safetyapp.auth.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

// CORREÇÃO: Definir o dataStore aqui no topo do arquivo para ele ser visível
private val Context.dataStore by preferencesDataStore(name = "auth_preferences")

class AuthPreferences(private val context: Context) {

    companion object {
        private val TOKEN_KEY = stringPreferencesKey("auth_token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
        private val USER_ID_KEY = stringPreferencesKey("user_id")
        private val USER_NAME_KEY = stringPreferencesKey("user_name")
        private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
    }

    // Flows
    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { !it[TOKEN_KEY].isNullOrBlank() }
    val authToken: Flow<String?> = context.dataStore.data.map { it[TOKEN_KEY] }
    val refreshToken: Flow<String?> = context.dataStore.data.map { it[REFRESH_TOKEN_KEY] }

    // Dados do usuário (Para substituir o UserPreferences antigo)
    val userName: Flow<String?> = context.dataStore.data.map { it[USER_NAME_KEY] }
    val userEmail: Flow<String?> = context.dataStore.data.map { it[USER_EMAIL_KEY] }

    suspend fun saveAuthData(token: String, refreshToken: String, userId: String, name: String, email: String) {
        context.dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
            prefs[REFRESH_TOKEN_KEY] = refreshToken
            prefs[USER_ID_KEY] = userId
            prefs[USER_NAME_KEY] = name
            prefs[USER_EMAIL_KEY] = email
        }
    }

    // Método usado pelo TokenManager
    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        context.dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = accessToken
            prefs[REFRESH_TOKEN_KEY] = refreshToken
        }
    }

    // CORREÇÃO: O nome correto que definimos é clearAuthData
    suspend fun clearAuthData() {
        context.dataStore.edit { it.clear() }
    }

    suspend fun getAccessToken(): String? {
        return context.dataStore.data.map { it[TOKEN_KEY] }.firstOrNull()
    }
}