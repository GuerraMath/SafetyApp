
package com.guerramath.safetyapp.auth.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "user_preferences"
)

/**
 * Preferências do usuário logado.
 * Armazena dados básicos para exibição rápida (sem precisar de API).
 */
class UserPreferences(private val context: Context) {

    companion object {
        private val USER_ID = stringPreferencesKey("user_id")
        private val USER_NAME = stringPreferencesKey("user_name")
        private val USER_EMAIL = stringPreferencesKey("user_email")
        private val USER_AVATAR_URL = stringPreferencesKey("user_avatar_url")
    }

    // ═══════════════════════════════════════════════════════════════════════
    // FLOWS (OBSERVÁVEIS)
    // ═══════════════════════════════════════════════════════════════════════

    val userId: Flow<String?> = context.userDataStore.data
        .map { it[USER_ID] }

    val userName: Flow<String?> = context.userDataStore.data
        .map { it[USER_NAME] }

    val userEmail: Flow<String?> = context.userDataStore.data
        .map { it[USER_EMAIL] }

    val userAvatarUrl: Flow<String?> = context.userDataStore.data
        .map { it[USER_AVATAR_URL] }

    // ═══════════════════════════════════════════════════════════════════════
    // GETTERS SÍNCRONOS
    // ═══════════════════════════════════════════════════════════════════════

    suspend fun getUserId(): String? = userId.first()
    suspend fun getUserName(): String? = userName.first()
    suspend fun getUserEmail(): String? = userEmail.first()
    suspend fun getUserAvatarUrl(): String? = userAvatarUrl.first()

    // ═══════════════════════════════════════════════════════════════════════
    // SETTERS
    // ═══════════════════════════════════════════════════════════════════════

    /** Salva dados do usuário */
    suspend fun saveUser(
        id: String,
        name: String,
        email: String,
        avatarUrl: String? = null
    ) {
        context.userDataStore.edit { preferences ->
            preferences[USER_ID] = id
            preferences[USER_NAME] = name
            preferences[USER_EMAIL] = email
            avatarUrl?.let { preferences[USER_AVATAR_URL] = it }
        }
    }

    /** Atualiza apenas o nome */
    suspend fun updateName(name: String) {
        context.userDataStore.edit { preferences ->
            preferences[USER_NAME] = name
        }
    }

    /** Atualiza apenas o email */
    suspend fun updateEmail(email: String) {
        context.userDataStore.edit { preferences ->
            preferences[USER_EMAIL] = email
        }
    }

    /** Atualiza avatar */
    suspend fun updateAvatarUrl(url: String?) {
        context.userDataStore.edit { preferences ->
            if (url != null) {
                preferences[USER_AVATAR_URL] = url
            } else {
                preferences.remove(USER_AVATAR_URL)
            }
        }
    }

    /** Limpa dados do usuário (logout) */
    suspend fun clearUser() {
        context.userDataStore.edit { preferences ->
            preferences.clear()
        }
    }

    /** Verifica se há usuário salvo */
    suspend fun hasUser(): Boolean {
        return getUserId() != null
    }
}

