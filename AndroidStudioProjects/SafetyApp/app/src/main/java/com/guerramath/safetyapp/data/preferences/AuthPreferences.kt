package com.guerramath.safetyapp.data.preferences

import android.content.Context
import android.content.SharedPreferences

class AuthPreferences(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    fun saveToken(token: String) {
        sharedPreferences.edit().putString(KEY_TOKEN, token).apply()
    }

    fun getToken(): String {
        return sharedPreferences.getString(KEY_TOKEN, "") ?: ""
    }

    fun clearToken() {
        sharedPreferences.edit().remove(KEY_TOKEN).apply()
    }

    fun saveUserId(userId: String) {
        sharedPreferences.edit().putString(KEY_USER_ID, userId).apply()
    }

    fun getUserId(): String {
        return sharedPreferences.getString(KEY_USER_ID, "") ?: ""
    }

    fun clearUserId() {
        sharedPreferences.edit().remove(KEY_USER_ID).apply()
    }

    fun clearAll() {
        sharedPreferences.edit().clear().apply()
    }

    companion object {
        private const val PREFS_NAME = "auth_preferences"
        private const val KEY_TOKEN = "auth_token"
        private const val KEY_USER_ID = "user_id"
    }
}