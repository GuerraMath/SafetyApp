
package com.guerramath.safetyapp.auth.biometric

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.biometricDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "biometric_preferences"
)

/**
 * Preferências relacionadas à autenticação biométrica.
 */
class BiometricPreferences(private val context: Context) {

    companion object {
        private val BIOMETRIC_ENABLED = booleanPreferencesKey("biometric_enabled")
        private val BIOMETRIC_FOR_LOGIN = booleanPreferencesKey("biometric_for_login")
        private val BIOMETRIC_FOR_SENSITIVE = booleanPreferencesKey("biometric_for_sensitive")
    }

    /** Se o usuário habilitou biometria no app */
    val isBiometricEnabled: Flow<Boolean> = context.biometricDataStore.data
        .map { preferences ->
            preferences[BIOMETRIC_ENABLED] ?: false
        }

    /** Se deve usar biometria para login */
    val useBiometricForLogin: Flow<Boolean> = context.biometricDataStore.data
        .map { preferences ->
            preferences[BIOMETRIC_FOR_LOGIN] ?: false
        }

    /** Se deve usar biometria para ações sensíveis */
    val useBiometricForSensitiveActions: Flow<Boolean> = context.biometricDataStore.data
        .map { preferences ->
            preferences[BIOMETRIC_FOR_SENSITIVE] ?: false
        }

    /** Habilita/desabilita biometria */
    suspend fun setBiometricEnabled(enabled: Boolean) {
        context.biometricDataStore.edit { preferences ->
            preferences[BIOMETRIC_ENABLED] = enabled
        }
    }

    /** Define se usa biometria para login */
    suspend fun setUseBiometricForLogin(use: Boolean) {
        context.biometricDataStore.edit { preferences ->
            preferences[BIOMETRIC_FOR_LOGIN] = use
        }
    }

    /** Define se usa biometria para ações sensíveis */
    suspend fun setUseBiometricForSensitiveActions(use: Boolean) {
        context.biometricDataStore.edit { preferences ->
            preferences[BIOMETRIC_FOR_SENSITIVE] = use
        }
    }

    /** Reseta todas as preferências de biometria */
    suspend fun clearAll() {
        context.biometricDataStore.edit { preferences ->
            preferences.clear()
        }
    }
}

