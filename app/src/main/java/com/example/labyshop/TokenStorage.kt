package com.example.labyshop

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.dataStore by preferencesDataStore(
    name = "auth_preferences"
)


class TokenStorage(
    private val context: Context
) {
    private val accessTokenKey = stringPreferencesKey("access_token")
    private val refreshTokenKey = stringPreferencesKey("refresh_token")

    suspend fun saveTokens(
        accessToken: String,
        refreshToken: String
    ) {
        context.dataStore.edit { preferences ->
            preferences[accessTokenKey] = accessToken
            preferences[refreshTokenKey] = refreshToken
        }
    }

    suspend fun getAccessToken(): String? {
        val preferences = context.dataStore.data.first()
        return preferences[accessTokenKey]
    }

    suspend fun getRefreshToken(): String? {
        val preferences = context.dataStore.data.first()
        return preferences[refreshTokenKey]
    }

    suspend fun clearTokens() {
        context.dataStore.edit { preferences ->
            preferences.remove(accessTokenKey)
            preferences.remove(refreshTokenKey)
        }
    }
}