package com.application.p3tskit.data.pref

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

class AuthPreferences private constructor(private val dataStore: DataStore<Preferences>) {

    suspend fun saveSession(user: AuthModel) {
        dataStore.edit { preferences ->
            preferences[EMAIL_KEY] = user.email
            preferences[TOKEN_KEY] = user.token
            preferences[LOGIN_KEY] = true
            preferences[LOGIN_TIME_KEY] = user.loginTime
        }
    }

    fun getSession(): Flow<AuthModel> {
        return dataStore.data.map { preferences ->
            AuthModel(
                preferences[EMAIL_KEY] ?: "",
                preferences[TOKEN_KEY] ?: "",
                preferences[LOGIN_KEY]?: false,
                preferences[LOGIN_TIME_KEY] ?: 0L
            )
        }
    }

    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: AuthPreferences? = null

        private val EMAIL_KEY = stringPreferencesKey("email")
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val LOGIN_KEY = booleanPreferencesKey("isLogin")
        private val LOGIN_TIME_KEY = longPreferencesKey("login_time")

        fun getInstance(context: Context): AuthPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = AuthPreferences(context.dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}
